package events;



import akka.actor.ActorRef;
import java.util.ArrayList;
import structures.GameState;
import events.gameplaystates.unitplaystates.AIUnitStateController;
import structures.basic.ComputerLogic.*;
import structures.basic.ComputerPlayer;

public class ComputerPlayerTurn {

	public void processComputerActions(ActorRef out, GameState g) {
		ComputerPlayer compPlayer = g.getPlayerTwo();
		AIUnitStateController controller = new AIUnitStateController(out, g, message);
		
		ArrayList<ComputerInstruction> cardsToPlay, monstersToMove, attacksToPerform;
		
		//cardsToPlay = compPlayer.playComputerCards();

		//monstersToMove = compPlayer.compPlayerMovesUnits();
		
		
		
		//iterate over list and call method in controller
		
		//iterate
		
		//method to return list of monsters to perform an attack (+ move if relevant)
		
		//iterate
	}
}
