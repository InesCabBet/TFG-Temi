package com.example.temitemplate

import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.robotemi.sdk.BatteryData
import com.robotemi.sdk.NlpResult
import com.robotemi.sdk.Robot
import com.robotemi.sdk.Robot.Companion.getInstance
import com.robotemi.sdk.SttLanguage
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.constants.HardButton
import com.robotemi.sdk.exception.OnSdkExceptionListener
import com.robotemi.sdk.exception.SdkException
import com.robotemi.sdk.face.ContactModel
import com.robotemi.sdk.face.OnContinuousFaceRecognizedListener
import com.robotemi.sdk.face.OnFaceRecognizedListener
import com.robotemi.sdk.listeners.OnBatteryStatusChangedListener
import com.robotemi.sdk.listeners.OnBeWithMeStatusChangedListener
import com.robotemi.sdk.listeners.OnButtonStatusChangedListener
import com.robotemi.sdk.listeners.OnConstraintBeWithStatusChangedListener
import com.robotemi.sdk.listeners.OnConversationStatusChangedListener
import com.robotemi.sdk.listeners.OnDetectionDataChangedListener
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener
import com.robotemi.sdk.listeners.OnDisabledFeatureListUpdatedListener
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnGreetModeStateChangedListener
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener
import com.robotemi.sdk.listeners.OnMovementStatusChangedListener
import com.robotemi.sdk.listeners.OnMovementVelocityChangedListener
import com.robotemi.sdk.listeners.OnPrivacyModeChangedListener
import com.robotemi.sdk.listeners.OnRobotDragStateChangedListener
import com.robotemi.sdk.listeners.OnRobotLiftedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnTelepresenceEventChangedListener
import com.robotemi.sdk.listeners.OnTtsVisualizerFftDataChangedListener
import com.robotemi.sdk.listeners.OnTtsVisualizerWaveFormDataChangedListener
import com.robotemi.sdk.listeners.OnUserInteractionChangedListener
import com.robotemi.sdk.map.OnLoadFloorStatusChangedListener
import com.robotemi.sdk.map.OnLoadMapStatusChangedListener
import com.robotemi.sdk.map.OnMapStatusChangedListener
import com.robotemi.sdk.model.CallEventModel
import com.robotemi.sdk.model.DetectionData
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener
import com.robotemi.sdk.navigation.listener.OnDistanceToDestinationChangedListener
import com.robotemi.sdk.navigation.listener.OnDistanceToLocationChangedListener
import com.robotemi.sdk.navigation.listener.OnReposeStatusChangedListener
import com.robotemi.sdk.navigation.model.Position
import com.robotemi.sdk.navigation.model.SpeedLevel
import com.robotemi.sdk.permission.OnRequestPermissionResultListener
import com.robotemi.sdk.permission.Permission
import com.robotemi.sdk.sequence.OnSequencePlayStatusChangedListener
import com.robotemi.sdk.voice.WakeupOrigin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

open class TemiActivity : AppCompatActivity(),
    Robot.NlpListener, OnRobotReadyListener,
    Robot.ConversationViewAttachesListener, Robot.WakeupWordListener,
    OnBeWithMeStatusChangedListener, OnGoToLocationStatusChangedListener,
    OnLocationsUpdatedListener, OnConstraintBeWithStatusChangedListener,
    OnDetectionStateChangedListener, Robot.AsrListener,
    OnTelepresenceEventChangedListener, OnRequestPermissionResultListener,
    OnDistanceToLocationChangedListener, OnCurrentPositionChangedListener,
    OnSequencePlayStatusChangedListener, OnRobotLiftedListener,
    OnDetectionDataChangedListener, OnUserInteractionChangedListener,
    OnFaceRecognizedListener, OnConversationStatusChangedListener,
    OnTtsVisualizerWaveFormDataChangedListener, OnTtsVisualizerFftDataChangedListener,
    OnReposeStatusChangedListener, OnLoadMapStatusChangedListener,
    OnDisabledFeatureListUpdatedListener, OnMovementVelocityChangedListener,
    OnMovementStatusChangedListener, OnContinuousFaceRecognizedListener,
    OnGreetModeStateChangedListener,
    TextToSpeech.OnInitListener, OnLoadFloorStatusChangedListener,
    OnDistanceToDestinationChangedListener, OnSdkExceptionListener,
    OnRobotDragStateChangedListener, OnMapStatusChangedListener,
    OnButtonStatusChangedListener, OnBatteryStatusChangedListener,
    OnPrivacyModeChangedListener, Robot.TtsListener
    /* The following listeners are omitted:
        * OnUsersUpdatedListener
        * OnTelepresenceStatusChangedListener
        * ITtsService
        * ActivityStreamPublishListener
    */
{
    protected lateinit var robot: Robot
    private var askQuestionCont: Continuation<String>? = null
    private var speakCont: Continuation<Unit>? = null
    private var goToCont: Continuation<Unit>? = null
    private var skidJoyCont: Continuation<Unit>? = null
    private var turnByCont: Continuation<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        robot = getInstance()
        /*robot.requestToBeKioskApp()
        if (robot.checkSelfPermission(Permission.SETTINGS) != Permission.GRANTED) {
            val permissions: MutableList<Permission> = ArrayList()
            permissions.add(Permission.SETTINGS)
            robot.requestPermissions(permissions, REQUEST_CODE_NORMAL)
        }*/
    }

    override fun onStart() {
        super.onStart()
        robot.addOnRobotReadyListener(this)
        robot.addOnRequestPermissionResultListener(this)
        robot.addOnTelepresenceEventChangedListener(this)
        robot.addOnFaceRecognizedListener(this)
        robot.addOnContinuousFaceRecognizedListener(this)
        robot.addOnLoadMapStatusChangedListener(this)
        robot.addOnDisabledFeatureListUpdatedListener(this)
        robot.addOnSdkExceptionListener(this)
        robot.addOnMovementStatusChangedListener(this)
        robot.addOnGreetModeStateChangedListener(this)
        robot.addOnLoadFloorStatusChangedListener(this)
        robot.addOnButtonStatusChangedListener(this)
        robot.addNlpListener(this)
        robot.addOnBeWithMeStatusChangedListener(this)
        robot.addOnGoToLocationStatusChangedListener(this)
        robot.addConversationViewAttachesListener(this)
        robot.addWakeupWordListener(this)
        robot.addTtsListener(this)
        robot.addOnLocationsUpdatedListener(this)
        robot.addOnConstraintBeWithStatusChangedListener(this)
        robot.addOnDetectionStateChangedListener(this)
        robot.addAsrListener(this)
        robot.addOnDistanceToLocationChangedListener(this)
        robot.addOnCurrentPositionChangedListener(this)
        robot.addOnSequencePlayStatusChangedListener(this)
        robot.addOnRobotLiftedListener(this)
        robot.addOnDetectionDataChangedListener(this)
        robot.addOnUserInteractionChangedListener(this)
        robot.addOnConversationStatusChangedListener(this)
        robot.addOnTtsVisualizerWaveFormDataChangedListener(this)
        robot.addOnTtsVisualizerFftDataChangedListener(this)
        robot.addOnReposeStatusChangedListener(this)
        robot.addOnMovementVelocityChangedListener(this)
        robot.addOnDistanceToDestinationChangedListener(this)
        robot.addOnRobotDragStateChangedListener(this)
        robot.addOnMapStatusChangedListener(this)
        robot.addOnBatteryStatusChangedListener(this)
        robot.addOnPrivacyModeStateChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        robot.removeOnRobotReadyListener(this)
        robot.removeOnRequestPermissionResultListener(this)
        robot.removeOnTelepresenceEventChangedListener(this)
        robot.removeOnFaceRecognizedListener(this)
        robot.removeOnContinuousFaceRecognizedListener(this)
        robot.removeOnLoadMapStatusChangedListener(this)
        robot.removeOnDisabledFeatureListUpdatedListener(this)
        robot.removeOnSdkExceptionListener(this)
        robot.removeOnMovementStatusChangedListener(this)
        robot.removeOnGreetModeStateChangedListener(this)
        robot.removeOnLoadFloorStatusChangedListener(this)
        robot.removeOnButtonStatusChangedListener(this)
        robot.removeNlpListener(this)
        robot.removeOnBeWithMeStatusChangedListener(this)
        robot.removeOnGoToLocationStatusChangedListener(this)
        robot.removeConversationViewAttachesListener(this)
        robot.removeWakeupWordListener(this)
        robot.removeTtsListener(this)
        robot.removeOnLocationsUpdateListener(this)
        robot.removeOnConstraintBeWithStatusChangedListener(this)
        robot.removeOnDetectionStateChangedListener(this)
        robot.removeAsrListener(this)
        robot.removeOnDistanceToLocationChangedListener(this)
        robot.removeOnCurrentPositionChangedListener(this)
        robot.removeOnSequencePlayStatusChangedListener(this)
        robot.removeOnRobotLiftedListener(this)
        robot.removeOnDetectionDataChangedListener(this)
        robot.removeOnUserInteractionChangedListener(this)
        robot.removeOnConversationStatusChangedListener(this)
        robot.removeOnTtsVisualizerWaveFormDataChangedListener(this)
        robot.removeOnTtsVisualizerFftDataChangedListener(this)
        robot.removeOnReposeStatusChangedListener(this)
        robot.removeOnMovementVelocityChangedListener(this)
        robot.removeOnDistanceToDestinationChangedListener(this)
        robot.removeOnRobotDragStateChangedListener(this)
        robot.removeOnMapStatusChangedListener(this)
        robot.removeOnBatteryStatusChangedListener(this)
        robot.removeOnPrivacyModeStateChangedListener(this)
    }


    /*************************************************************
     * Robot custom functions
     ************************************************************/

    suspend fun awaitAskQuestion(text: String): String = suspendCancellableCoroutine { cont ->
        askQuestionCont = cont
        cont.invokeOnCancellation { askQuestionCont = null }
        robot.askQuestion(TtsRequest.create(text, isShowOnConversationLayer = false))
    }

    fun askQuestion(text: String, onResult: ((String) -> Unit)? = null) {
        lifecycleScope.launch {
            val result = awaitAskQuestion(text)
            delay(1000L)
            onResult?.invoke(result)
        }
    }

    suspend fun awaitSpeak(speech: String,
                           isShowOnConversationLayer: Boolean = true,
                           language: TtsRequest.Language,
                           showAnimationOnly: Boolean = false,
                           cached: Boolean = false
    ): Unit = suspendCancellableCoroutine { cont ->
        speakCont = cont
        cont.invokeOnCancellation { askQuestionCont = null }
        robot.speak(TtsRequest.create(
            speech,
            isShowOnConversationLayer = isShowOnConversationLayer,
            language = language,
            showAnimationOnly = showAnimationOnly,
            cached = cached
        ))
    }

    fun speak(speech: String,
              isShowOnConversationLayer: Boolean = true,
              language: TtsRequest.Language,
              showAnimationOnly: Boolean = false,
              cached: Boolean = false,
              onResult: ((Unit) -> Unit)? = null) {
        lifecycleScope.launch {
            awaitSpeak(speech, isShowOnConversationLayer, language, showAnimationOnly, cached)
            delay(1000L)
            onResult?.invoke(Unit)
        }
    }

    suspend fun awaitSpeak(speech: String,
                           isShowOnConversationLayer: Boolean = true,
                           showAnimationOnly: Boolean = false,
                           cached: Boolean = false
    ): Unit = suspendCancellableCoroutine { cont ->
        speakCont = cont
        cont.invokeOnCancellation { speakCont = null }
        robot.speak(TtsRequest.create(
            speech,
            isShowOnConversationLayer = isShowOnConversationLayer,
            showAnimationOnly = showAnimationOnly,
            cached = cached
        ))
    }

    fun speak(speech: String,
              isShowOnConversationLayer: Boolean = true,
              showAnimationOnly: Boolean = false,
              cached: Boolean = false,
              onResult: ((Unit) -> Unit)? = null) {
        lifecycleScope.launch {
            awaitSpeak(speech, isShowOnConversationLayer, showAnimationOnly, cached)
            delay(1000L)
            onResult?.invoke(Unit)
        }
    }

    suspend fun awaitGoTo(location: String,
                          backwards: Boolean = false,
                          noByPass: Boolean? = null ,
                          speedLevel: SpeedLevel? = null,
                          highAccuracyArrival: Boolean = false,
                          noRotationAtEnd: Boolean = false):
            Unit = suspendCancellableCoroutine { cont ->
        goToCont = cont
        cont.invokeOnCancellation { goToCont = null }
        robot.goTo(location, backwards, noByPass, speedLevel, highAccuracyArrival, noRotationAtEnd)
    }

    fun goTo(location: String,
              backwards: Boolean = false,
              noByPass: Boolean? = null ,
              speedLevel: SpeedLevel? = null,
              highAccuracyArrival: Boolean = false,
              noRotationAtEnd: Boolean = false,
              onResult: ((Unit) -> Unit)? = null) {
        lifecycleScope.launch {
            awaitGoTo(location, backwards,
                      noByPass, speedLevel,
                      highAccuracyArrival,
                      noRotationAtEnd)
            delay(1000L)
            onResult?.invoke(Unit)
        }
    }


    suspend fun awaitGoTo(position: Position,
                          backwards: Boolean = false,
                          noByPass: Boolean? = null ,
                          speedLevel: SpeedLevel? = null,
                          highAccuracyArrival: Boolean = false):
            Unit = suspendCancellableCoroutine { cont ->
        goToCont = cont
        cont.invokeOnCancellation { goToCont = null }
        robot.goToPosition(position, backwards, noByPass, speedLevel, highAccuracyArrival)
    }

    fun goTo(position: Position,
              backwards: Boolean = false,
              noByPass: Boolean? = null ,
              speedLevel: SpeedLevel? = null,
              highAccuracyArrival: Boolean = false,
              onResult: ((Unit) -> Unit)? = null) {
        lifecycleScope.launch {
            awaitGoTo(position, backwards, noByPass, speedLevel, highAccuracyArrival)
            delay(1000L)
            onResult?.invoke(Unit)
        }
    }

    suspend fun awaitSkidJoy(x: Float,
                             y: Float,
                             smart: Boolean = true):
            Unit = suspendCancellableCoroutine { cont ->
        skidJoyCont = cont
        cont.invokeOnCancellation { skidJoyCont = null }
        robot.skidJoy(x, y, smart)
    }

    fun skidJoy(x: Float,
                y: Float,
                smart: Boolean = true,
                onResult: ((Unit) -> Unit)? = null) {
        lifecycleScope.launch {
            awaitSkidJoy(x, y, smart)
            delay(1000L)
            onResult?.invoke(Unit)
        }
    }

    suspend fun awaitTurnBy(degrees: Int,
                            speed: Float):
            Unit = suspendCancellableCoroutine { cont ->
        turnByCont = cont
        cont.invokeOnCancellation { turnByCont = null }
        robot.turnBy(degrees, speed)
    }

    fun turnBy(degrees: Int,
               speed: Float,
               onResult: ((Unit) -> Unit)? = null) {
        lifecycleScope.launch {
            awaitTurnBy(degrees, speed)
            delay(1000L)
            onResult?.invoke(Unit)
        }
    }

    suspend fun awaitTurnBy(degrees: Int):
            Unit = suspendCancellableCoroutine { cont ->
        turnByCont = cont
        cont.invokeOnCancellation { turnByCont = null }
        robot.turnBy(degrees)
    }

    fun turnBy(degrees: Int,
               onResult: ((Unit) -> Unit)? = null) {
        lifecycleScope.launch {
            awaitTurnBy(degrees)
            delay(1000L)
            onResult?.invoke(Unit)
        }
    }

    /*************************************************************
     * Robot listeners
     *************************************************************/
    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            try {
                val activityInfo = packageManager.getActivityInfo(
                    componentName,
                    PackageManager.GET_META_DATA
                )
                robot.onStart(activityInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                throw RuntimeException(e)
            }
        }
    }

    final override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
        askQuestionCont?.let { // There is a pending question
            askQuestionCont?.resume(asrResult)
            askQuestionCont = null
            robot.finishConversation()
        }
    }

    override fun onConversationStatusChanged(status: Int, text: String) {
        return
    }

    override fun onUserInteraction(isInteracting: Boolean) {
        return
    }

    override fun onNlpCompleted(nlpResult: NlpResult) {
        return
    }

    override fun onConversationAttaches(isAttached: Boolean) {
        return
    }

    override fun onWakeupWord(
        wakeupWord: String,
        direction: Int,
        origin: WakeupOrigin
    ) {
        return
    }

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        if (ttsRequest.status == TtsRequest.Status.COMPLETED) { // There is a pending question
            speakCont?.let { // There is a pending question
                speakCont?.resume(Unit)
                speakCont = null
            }
        }
        return
    }

    override fun onBeWithMeStatusChanged(status: String) {
        return
    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        if (status == OnGoToLocationStatusChangedListener.COMPLETE) {
            goToCont?.let { // There is a pending question
                goToCont?.resume(Unit)
                goToCont = null
            }
        }
    }

    override fun onLocationsUpdated(locations: List<String>) {
        return
    }

    override fun onConstraintBeWithStatusChanged(isConstraint: Boolean) {
        return
    }

    override fun onDetectionStateChanged(state: Int) {
        return
    }

    override fun onTelepresenceEventChanged(callEventModel: CallEventModel) {
        return
    }

    override fun onRequestPermissionResult(
        permission: Permission,
        grantResult: Int,
        requestCode: Int
    ) {
        Log.v("DEBUG", "permission $permission result $grantResult request code $requestCode")
    }

    override fun onDistanceToLocationChanged(distances: Map<String, Float>) {
        return
    }

    override fun onCurrentPositionChanged(position: Position) {
        return
    }

    override fun onSequencePlayStatusChanged(status: Int, sequenceId: String?) {
        return
    }

    override fun onRobotLifted(isLifted: Boolean, reason: String) {
        return
    }

    override fun onDetectionDataChanged(detectionData: DetectionData) {
        return
    }

    override fun onFaceRecognized(contactModelList: List<ContactModel>) {
        return
    }

    override fun onTtsVisualizerWaveFormDataChanged(waveForm: ByteArray) {
        return
    }

    override fun onTtsVisualizerFftDataChanged(fft: ByteArray) {
        return
    }

    override fun onReposeStatusChanged(status: Int, description: String) {
        return
    }

    override fun onLoadMapStatusChanged(status: Int, requestId: String) {
        return
    }

    override fun onDisabledFeatureListUpdated(disabledFeatureList: List<String>) {
        return
    }

    override fun onMovementVelocityChanged(velocity: Float) {
        return
    }

    override fun onMovementStatusChanged(type: String, status: String) {
        if (type == OnMovementStatusChangedListener.TYPE_SKID_JOY
            && status == OnMovementStatusChangedListener.STATUS_COMPLETE) {
            skidJoyCont?.let { // There is a pending question
                skidJoyCont?.resume(Unit)
                skidJoyCont = null
            }
        } else if (type == OnMovementStatusChangedListener.TYPE_TURN_BY
                   && status == OnMovementStatusChangedListener.STATUS_COMPLETE) {
            turnByCont?.let { // There is a pending question
                turnByCont?.resume(Unit)
                turnByCont = null
            }
        }
        return
    }

    override fun onContinuousFaceRecognized(contactModelList: List<ContactModel>) {
        return
    }

    override fun onGreetModeStateChanged(state: Int) {
        return
    }

    override fun onInit(status: Int) {
        return
    }

    override fun onLoadFloorStatusChanged(status: Int) {
        return
    }

    override fun onDistanceToDestinationChanged(location: String, distance: Float) {
        return
    }

    override fun onSdkError(sdkException: SdkException) {
        return
    }

    override fun onRobotDragStateChanged(isDragged: Boolean) {
        return
    }

    override fun onMapStatusChanged(isLost: Boolean, isLocked: Boolean) {
        return
    }

    override fun onButtonStatusChanged(
        hardButton: HardButton,
        status: HardButton.Status
    ) {
        return
    }

    override fun onBatteryStatusChanged(batteryData: BatteryData?) {
        return
    }

    override fun onPrivacyModeChanged(state: Boolean) {
        return
    }
}