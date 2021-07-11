package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that a unit instance has started a move. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = “unitMoving”
 *   id = <unit id>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class UnitMoving implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int unitid = message.get("id").asInt();

		// Lock UI and set user moving flag to true 
		// (done also in Unit states since this sometimes doesnt execute fast enoguh)
		/**===========================**/
		gameState.userinteractionLock();
		/**===========================**/

		gameState.setUnitMovingFlag(true);
	}

}
