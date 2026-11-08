package com.example.temitemplate

import android.os.Bundle
import android.widget.Button

class MainActivity : TemiActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRobotReady(isReady: Boolean) {
        super.onRobotReady(isReady)
        if (isReady) {
            val button_r = findViewById<Button>(R.id.run_button)
            button_r.setOnClickListener {
                /********************************
                 * Acompañamiento
                 ********************************/
                // robot.beWithMe() // Acompañamiento completo

                // robot.constraintBeWith() // Acompañamiento restringido (Solo rota)

                /********************************
                 * Posicionamiento - gestión de ubicaciones
                 ********************************/
                /*
                // Ubicaciones definidas
                val locations_1 = robot.locations
                for (location in locations_1) {
                    Log.v("DEBUG", location)
                }
                */

                // robot.saveLocation("TEST") // Guardar nueva ubicación

                // val res =  robot.deleteLocation("TEST") // Eliminar ubicación

                /********************************
                 * Posicionamiento - desplazamientos
                 ********************************/
                //val position = robot.getPosition() // Posición actual

                /*
                lifecycleScope.launch {
                    awaitGoTo("pos1") // Ir a ubicación
                    delay(1000L)
                    awaitGoTo(Position(x=2.073741f, // Ir a posición con coordenadas
                                       y=-1.684549f,
                                       yaw=0.307778f,
                                       tiltAngle=47,
                                       isInMapArea=true))
                }
                */

                /********************************
                 * Control de actuadores
                 ********************************/

                /*
                lifecycleScope.launch {
                    awaitSkidJoy(1.0f, 0.0f)
                    delay(100L)
                    awaitTurnBy(30, 1.0f)
                    delay(100L)
                    awaitTurnBy(-30)
                    delay(100L)
                    robot.tiltAngle(-30)
                    delay(1000L)
                    robot.tiltBy(-60)
                }
                */

                //robot.patrol(listOf<String>("pos0", "pos1", "pos2", "pos3"), nonstop = true, times=10, waiting = 3)

                /********************************
                 * Speech
                 ********************************/
                /*
                lifecycleScope.launch {
                    awaitSpeak("Primera frase")
                    // Do something
                    awaitSpeak("Segunda frase")
                }
                */

                /*lifecycleScope.launch {
                    val name = awaitAskQuestion(text = "¿Hola, cómo te llamas?")
                    delay(1000L)
                    val greeting = if (robot.getTtsVoice()?.gender == Gender.FEMALE) "Encantada" else "Encantado"
                    robotSpeak("$greeting de conocerte, $name")
                }*/

                // robot.wakeup(listOf(SttLanguage.ES_ES, SttLanguage.EN_US), WakeupRequest())

                /********************************
                 * Telepresencia
                 ********************************/

                /*
                val CONTACT_TYPES = mapOf<Int, String>(
                    0 to "admin",
                    1 to "collaborator",
                    2 to "guest",
                    3 to "registered user",
                    10 to "unregistered user"
                )

                val contacts = robot.allContact
                for (contact in contacts) {
                    Log.v("DEBUG", "Contact ${contact.name} has id ${contact.userId} and type ${CONTACT_TYPES[contact.role]}")
                }
                */

                //Log.v("DEBUG", "Temi admin in ${robot.adminInfo?.name} with ID ${robot.adminInfo?.userId}")

                /*
                val availability = robot.membersStatus
                for (member in availability) {
                    var message = "Member ${member.memberId} "
                    if (member.centerStatus > 0 && member.mobileStatus > 0) {
                        message += "is not available."
                    } else {
                        message += "is available in "
                        if (member.centerStatus == 0 && member.mobileStatus == 0) {
                            message += "both mobile app and Temi center."
                        } else if (member.centerStatus == 0) {
                            message += "Temi center."
                        } else {
                            message += "mobile app."
                        }
                    }
                    Log.v("DEBUG", message)
                }
                */

                /*
                val user_id = "de444965a99391b43b73a6defd9-----" // El id lo conseguimos de .allContact, o adminInfo
                robot.startTelepresence(displayName = "Oscar", peerId = user_id, platform = Platform.MOBILE) // Deprecada pero no hay otra alternativa
                */

                /********************************
                 * Sistema
                 ********************************/

                // robot.showTopBar()

                // Log.v("DEBUG", "Hi, my name is ${robot.getNickName()}")

                // Log.v("DEBUG", "Temi is in ${robot.getMode().toString().lowercase()} mode.")
            }

            val button_s = findViewById<Button>(R.id.stop_button)
            button_s.setOnClickListener {
                robot.stopMovement()
                robot.cancelAllTtsRequests()

                // robot.hideTopBar()
            }
        }
    }
}