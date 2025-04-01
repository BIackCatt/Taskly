package com.example.todolist.main.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.backend.model.CollabTask
import com.example.todolist.backend.model.Collaboration
import com.example.todolist.backend.model.CollaborationDb
import com.example.todolist.backend.db.CollaborationsRepo
import com.example.todolist.backend.model.Operation
import com.example.todolist.backend.utils.convertCollabTaskToMap
import com.example.todolist.backend.utils.convertCollabToMap
import com.example.todolist.backend.utils.convertUserDataToMap
import com.example.todolist.backend.firebase.CollaborationsFireStore
import com.example.todolist.backend.firebase.UserFireStore
import com.example.todolist.backend.firebase.toMemberData
import com.example.todolist.backend.auth.AuthenticatedUserData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CollaborationViewModel(
    private val collaborationsRepo: CollaborationsRepo,
    private val fireStore: FirebaseFirestore,
) : ViewModel() {

    private val _loading: MutableStateFlow<LoadingControl> =
        MutableStateFlow(LoadingControl.Success)
    val loading = _loading.asStateFlow()

    private val database = CollaborationsFireStore(fireStore)
    private val userDatabases = UserFireStore(fireStore = fireStore)
    private val _userCollab = MutableStateFlow(CollabsState())
    val userCollab = _userCollab.asStateFlow()
    val lastKnownTasksState = mutableMapOf<String, CollabTask>()
    private var userCollabsListener: ListenerRegistration? = null
    private val collabListeners = mutableMapOf<String, List<ListenerRegistration>>()

    fun updateCurrentCollab(collab: Collaboration) {
        _userCollab.update {
            it.copy(
                currentCollab = collab
            )
        }
    }

    fun addTask(
        task: CollabTask,
        collab: Collaboration,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val collabId = _userCollab.value.currentCollab?.id
        if (collabId != null) {
            database.addTask(
                collaborationId = collabId, task = convertCollabTaskToMap(task),
                onError = onError, onSuccess = {
                    onSuccess.invoke()
                    logOperation(
                        userId = task.assignedTo["id"] as String,
                        username = task.assignedTo["username"] as String,
                        userPic = task.assignedTo["profilePic"] as String,
                        collabId = collabId,
                        onSuccess = {},
                        onError = onError,
                        message = "Added \"${task.title}\""
                    )
                }
            )
        }
    }

    fun updateTask(
        user: AuthenticatedUserData,
        task: CollabTask,
        collabId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val collabRef = fireStore.collection("collaboration").document(collabId)
        collabRef.collection("tasks").document(task.id).set(convertCollabTaskToMap(task))
            .addOnSuccessListener {
                onSuccess()
                logOperation(
                    userId = user.userId,
                    username = user.username?: "Unknown member",
                    userPic = user.profilePictureUrl,
                    collabId = collabId,
                    onSuccess = {},
                    onError = onError,
                    message = "Edited \"${task.title}\""
                )
            }
            .addOnFailureListener { onError("Something Went wrong!")}

    }

    fun deleteTask(
        user: AuthenticatedUserData,
        task: CollabTask,
        collab: Collaboration,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val collabId = collab.id // Get the current state
        if (_userCollab.value.currentCollab?.tasks?.map { it?.id }?.contains(task.id) == true) {
            Log.d("CollabTasks", "Deleting uncomp task: $task")
            database.deleteTask(
                collabId = collabId,
                task = convertCollabTaskToMap(task),
                onError = onError,
                onSuccess = {
                    onSuccess.invoke()
                    logOperation(
                        userId = user.userId,
                        username = user.username?: "Unknown member",
                        userPic = user.profilePictureUrl,
                        collabId = collabId,
                        onSuccess = {},
                        onError = onError,
                        message = "Deleted \"${task.title}\" (uncompleted)"
                    )
                }
            )
        } else {
            Log.d("CollabTasks", "Deleting completed task: $task")
            database.deleteCompletedTask(
                collabId = collabId,
                task = convertCollabTaskToMap(task),
                onError = onError,
                onSuccess = {
                    onSuccess.invoke()
                    logOperation(
                        userId = user.userId,
                        username = user.username?: "Unknown member",
                        userPic = user.profilePictureUrl,
                        collabId = collabId,
                        onSuccess = {},
                        onError = onError,
                        message = "Deleted \"${task.title}\" (completed)"
                    )
                }
            )
        }
    }

    fun completeTask(
        user: AuthenticatedUserData,
        collabId: String,
        task: CollabTask,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val collabRef = fireStore.collection("collaboration").document(collabId)
        // First, remove from tasks
        collabRef.collection("tasks").document(task.id).delete()
            .addOnSuccessListener {
                collabRef.collection("completed_tasks").document(task.id).set(task)
                    .addOnSuccessListener {
                        onSuccess()
                        logOperation(
                            userId = user.userId,
                            username = user.username?: "Unknown member",
                            userPic = user.profilePictureUrl,
                            collabId = collabId,
                            onSuccess = {},
                            onError = onError,
                            message = "Completed \"${task.title}\""
                        )
                    }
                    .addOnFailureListener { e -> onError(e.message.toString()) }
            }
            .addOnFailureListener { e -> onError(e.message.toString()) }
    }


    fun uncompleteTask(
        user: AuthenticatedUserData,
        collabId: String,
        task: CollabTask,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val collabRef = fireStore.collection("collaboration").document(collabId)

        // First, add back to tasks
        collabRef.collection("tasks").document(task.id).set(task)
            .addOnSuccessListener {
                // Then, delete from completed_tasks
                collabRef.collection("completed_tasks").document(task.id).delete()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError("Failed to delete from completed_tasks: ${e.message}") }
                logOperation(
                    userId = user.userId,
                    username = user.username?: "Unknown member",
                    userPic = user.profilePictureUrl,
                    collabId = collabId,
                    onSuccess = {},
                    onError = onError,
                    message = "Uncompleted \"${task.title}\""
                )
            }
            .addOnFailureListener { e -> onError("Failed to add back to tasks: ${e.message}") }
    }

    private fun logOperation(
        userId: String?,
        collabId: String,
        username: String,
        userPic: String?,
        message: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val operation = mapOf(
            "id" to UUID.randomUUID().toString(),
            "userId" to userId,
            "userPic" to userPic,
            "username" to username,
            "timestamp" to FieldValue.serverTimestamp(),
            "action" to message
        )
        database.addOperation(
            operation = operation,
            collabId = collabId,
            onSuccess = onSuccess,
            onError = onError
        )

    }

    fun clearOperationsHistory(collabId: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            database.clearOperations(collabId, onSuccess, onError)
        }
    }
    fun deleteOperation(collabId: String, operationId: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            database.deleteOperation(
                collabId = collabId,
                operationId = operationId,
                onError = onError,
                onSuccess = onSuccess
            )
        }
    }

    fun addCollaboration(
        userData: AuthenticatedUserData,
        onIsExist: () -> Unit,
        collab: Collaboration,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit,
    ) {

        // Check if the collaboration username already exists
        database.checkIsExist(collab.username) { exists ->
            if (exists) {
                onIsExist.invoke()
            } else {
                viewModelScope.launch {

                    val collaboration = Collaboration(
                        id = collab.id,
                        username = collab.username,
                        password = collab.password,
                        members = mutableListOf(userData.toMemberData()),
                        admins = mutableListOf(userData.toMemberData()),
                        tasks = mutableListOf()
                    )
                    database.addCollaboration(
                        collaboration = convertCollabToMap(collaboration),
                        onError = { error ->
                            onError(error) // Handle error if adding collaboration fails
                        },
                        onSuccess = { id, name ->
                            onSuccess.invoke()
                            logOperation(
                                userId = userData.userId,
                                username = userData.username?: "Unknown Member",
                                userPic = userData.profilePictureUrl,
                                collabId = id,
                                onSuccess = {},
                                onError = onError,
                                message = "created collaboration \"${name}\""
                            )
                        }
                    )

                    userData.collaborations.add(collaboration.id)
                    // Update the user's collaborations list

                    _userCollab.update {
                        it.copy(
                            collabs = it.collabs.plus(collaboration)
                        )
                    }
                    collaborationsRepo.insert(collaboration.toCollaborationDb())
                    val convertedData = convertUserDataToMap(userData)
                    // Update the user's data in Firestore
                    if (convertedData != null) {
                        Log.d("AddingCollab", convertedData.toString())
                        userDatabases.addUserData(
                            userId = userData.userId,
                            userData = convertedData,
                            onError = onError
                        )
                    } else {
                        onError("Failed to convert user data")
                    }
                }
            }
        }
    }

    private fun deleteCollab(
        collabId: String,
        onError: (String) -> Unit,
    ) {
        fireStore.collection("collaboration").document(collabId)
            .delete()
            .addOnFailureListener {
                onError("Something went wrong")
            }
    }

    fun joinCollab(
        userId: String?,
        username: String,
        password: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit,
        userData: AuthenticatedUserData?,
    ) {
        if (userId == null) {
            onError("User ID is null")
            return
        }

        viewModelScope.launch {
            val authResult = database.checkAuth(username, password)
            if (authResult.isEmpty()) {
                onError("Authentication failed")
                return@launch
            }

            val collaboration = authResult[0]
            val joinResult = database.joinCollab(userData, collaboration, onError)
            if (!joinResult) {
                onError("Failed to join collaboration")
                return@launch
            }
            onSuccess()
            logOperation(
                userId = userData?.userId,
                username = userData?.username?: "Unknown collabmate",
                userPic = userData?.profilePictureUrl,
                collabId = collaboration.id,
                onSuccess = {},
                onError = onError,
                message = "joined the collab"
            )
            userData?.collaborations?.add(collaboration.id)
            val convertedData = convertUserDataToMap(userData)
            userDatabases.addUserData(userId, convertedData, onError)
        }
    }

    fun promoteUser(
        user: AuthenticatedUserData?,
        memberId: String?,
        memberUsername: String?,
        collab: Collaboration,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        var member: Map<String, String?>? = null
        collab.members.forEach { currentMember ->
            if ((currentMember?.get("id") as String) == memberId) {
                member = currentMember
            }
        }
        if (member == null) {
            onError("Member not found")
            return
        }
        if (collab.admins.contains(member)) {
            onError("${member?.get("username")} is already admin")
            return
        } else {
            viewModelScope.launch {
                collab.admins.add(member)
                database.updateCollaboration(collab, onError) {
                    onSuccess()
                    logOperation(
                        userId = user?.userId,
                        username = user?.username ?: "Unknown Member",
                        userPic = user?.profilePictureUrl,
                        collabId = collab.id,
                        onSuccess = {},
                        onError = onError,
                        message = "promoted $memberUsername to Taskmaster"
                    )
                }
            }
        }
    }

    fun exitCollab(
        user: AuthenticatedUserData?, collab: Collaboration, onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        var member: Map<String, String?>? = null
        collab.members.forEach { currentMemeber ->
            if (user?.userId == currentMemeber?.get("id")) {
                member = currentMemeber
            }
        }
        if (member == null) {
            onError("Couldn't find the user")
            return
        } else {
            viewModelScope.launch {
                try {
                    // Check if the member is an admin
                    var log by mutableStateOf(false)
                    val isAdmin = collab.admins.contains(member)
                    val isOnlyAdmin = isAdmin && collab.admins.size == 1
                    val isOnlyMember = collab.members.size == 1

                    // Handle admin-specific cases
                    if (isAdmin) {
                        if (isOnlyAdmin && !isOnlyMember) {
                            onError("You are the only taskmaster, you must promote any member before leaving!")
                            return@launch
                        }
                        collab.admins.remove(member)
                    }

                    // Remove the member from the collaboration
                    collab.members.remove(member)

                    // Update or delete the collaboration based on conditions
                    if (isOnlyAdmin || isOnlyMember) {
                        deleteCollab(collab.id, onError)
                        log = false
                    } else {
                        database.updateCollaboration(collab, onError, onSuccess = {})
                    }

                    // Remove the collaboration from the user's list
                    userDatabases.exitCollab(user, onError, collab.id)

                    // Update the local state
                    _userCollab.update { state ->
                        state.copy(
                            collabs = state.collabs.filter { it?.id != collab.id },
                            currentCollab = null
                        )
                    }

                    // Log the operation
                    if (log) {
                        logOperation(
                            userId = user?.userId,
                            username = user?.username ?: "Unknown collabmate",
                            userPic = user?.profilePictureUrl,
                            collabId = collab.id,
                            onSuccess = {},
                            onError = onError,
                            message = "left the collab"
                        )
                    }

                    // Invoke success callback
                    onSuccess.invoke()
                } catch (e: Exception) {
                    onError(e.message ?: "An error occurred")
                }
            }
        }
    }

    fun removeMember(
        user: AuthenticatedUserData?,
        memberId: String?,
        memberUsername: String?,
        collab: Collaboration,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        var member: Map<String, String?>? = null
        collab.members.forEach { currentMember ->
            if (memberId == currentMember?.get("id")) {
                member = currentMember
            }
        }
        if (member == null) {
            onError("Couldn't find the user")
            return
        } else {
            viewModelScope.launch {
                try {
                    // Check if the member is an admin
                    val isAdmin = collab.admins.contains(member)
                    // Handle admin-specific cases
                    if (isAdmin) {
                        collab.admins.remove(member)
                    }

                    // Remove the member from the collaboration
                    collab.members.remove(member)
                    database.updateCollaboration(collab, onError, onSuccess)
                    val memberData = userDatabases.getUserData(memberId, onError)
                    userDatabases.exitCollab(memberData, onError = onError, collab.id)

                    // Log the operation
                    logOperation(
                        userId = user?.userId,
                        username = user?.username ?: "Unknown collabmate",
                        userPic = user?.profilePictureUrl,
                        collabId = collab.id,
                        onSuccess = {},
                        onError = onError,
                        message = "Kicked $memberUsername from the collab"
                    )

                    // Invoke success callback
                    onSuccess.invoke()
                } catch (e: Exception) {
                    onError(e.message ?: "An error occurred")
                }
            }
        }
    }

    fun signOut() {
        _userCollab.update {
            it.copy(
                collabs = listOf(),
                currentCollab = null
            )
        }
        userCollabsListener?.remove()
        collabListeners.values.forEach { it.forEach { listener -> listener.remove() } }
    }

    fun getUserCollabs(
        userId: String?,
        onError: (String) -> Unit = {},
        onSuccess: (List<String?>) -> Unit,
        onTaskChange: (String) -> Unit
    ) {
        if (userId == null) {
            onError("No provided user")
            return
        }

        val userRef = fireStore.collection("users").document(userId)

        // Stop previous listener if exists
        userCollabsListener?.remove()

        userCollabsListener = userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError("Failed to listen to user collaborations: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot == null || !snapshot.exists()) {
                onError("User not found")
                return@addSnapshotListener
            }
            viewModelScope.launch {
                val data = snapshot.get("data") as? Map<String, Any>
                val collabIds = data?.get("collaborations") as? List<String> ?: emptyList()

                onSuccess(collabIds)

                // For each collaboration ID, attach a listener for its tasks subcollection
                collabIds.forEach { collabId ->
                    observeCollab(collabId, onError)
                }
            }
        }
    }

    private fun observeCollab(
        collabId: String,
        onError: (String) -> Unit
    ) {
        // Remove old listeners
        collabListeners[collabId]?.forEach { it.remove() }
        collabListeners.remove(collabId)

        val collabRef = fireStore.collection("collaboration").document(collabId)
        // 🔹 Listen for Collaboration Data
        val collabListener = collabRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError("Failed to listen to collaboration: ${error.message}")
                return@addSnapshotListener
            }

            val collab = snapshot?.toObject(Collaboration::class.java)
            if (collab != null) {
                if (collab.id in _userCollab.value.collabs.map { it?.id }) {
                    _userCollab.update { state ->
                        state.copy(collabs = state.collabs.map { if (it?.id == collabId) collab else it })
                    }
                } else {
                    _userCollab.update {
                        it.copy(collabs = it.collabs.plus(collab))
                    }
                }
            }
        }
        val completedTasksListener = collabRef.collection("completed_tasks").addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError("Failed to listen to completed tasks: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot == null) return@addSnapshotListener

            // Convert Firestore snapshot to completed task objects
            val completedTasks = snapshot.documents.mapNotNull { it.toObject(CollabTask::class.java) }

            _userCollab.update { state ->
                val updatedCollabs = state.collabs.map {
                    if (it?.id == collabId) it.copy(completedTasks = completedTasks) else it
                }

                val updatedCurrentCollab = if (state.currentCollab?.id == collabId) {
                    state.currentCollab.copy(completedTasks = completedTasks)
                } else {
                    state.currentCollab
                }

                state.copy(collabs = updatedCollabs, currentCollab = updatedCurrentCollab)
            }
        }
        // 🔹 Listen for Tasks (Uncompleted)
        val tasksListener = collabRef.collection("tasks").addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError("Failed to listen to tasks: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot == null) return@addSnapshotListener

            // Convert Firestore snapshot to task objects
            val tasks = snapshot.documents.mapNotNull { it.toObject(CollabTask::class.java) }

            _userCollab.update { state ->
                val updatedCollabs = state.collabs.map {
                    if (it?.id == collabId) it.copy(tasks = tasks) else it
                }

                val updatedCurrentCollab = if (state.currentCollab?.id == collabId) {
                    state.currentCollab.copy(tasks = tasks)
                } else {
                    state.currentCollab
                }

                state.copy(collabs = updatedCollabs, currentCollab = updatedCurrentCollab)
            }
        }
        val operationListener = collabRef.collection("operations")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by timestamp (newest first)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError("Failed to listen to operations: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                // Convert documents to Operation objects
                val operations = snapshot.documents.mapNotNull { doc ->
                    Operation(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        username = doc.getString("username") ?: "",
                        userPic = doc.getString("userPic") ?: "",
                        message = doc.getString("action")?: "",
                        timestamp = doc.getTimestamp("timestamp")?.toDate() ?: Date()
                    )
                }

                // Update state
                _userCollab.update { state ->
                    val updatedCollabs = state.collabs.map {
                        if (it?.id == collabId) it.copy(operations = operations) else it
                    }

                    val updatedCurrentCollab = if (state.currentCollab?.id == collabId) {
                        state.currentCollab.copy(operations = operations)
                    } else {
                        state.currentCollab
                    }

                    state.copy(collabs = updatedCollabs, currentCollab = updatedCurrentCollab)
                }
            }
        // Store listeners to avoid duplicates
        collabListeners[collabId] = mutableListOf(collabListener, tasksListener, completedTasksListener, operationListener)
    }
}
data class CollabsState(
    val collabs: List<Collaboration?> = listOf(),
    val currentCollab: Collaboration? = null,
    val currentCollabUsers: Map<String, AuthenticatedUserData?> = mapOf()
)



fun Collaboration.toCollaborationDb(): CollaborationDb = CollaborationDb(
    id = this.id,
    username = this.username
)