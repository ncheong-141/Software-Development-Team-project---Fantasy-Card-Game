package events;



import akka.actor.ActorRef;
import java.util.ArrayList;
import structures.GameState;
import events.gameplaystates.unitplaystates.AIUnitStateController;
import structures.basic.ComputerLogic.*;
import structures.basic.ComputerPlayer;
import structures.basic.Tile;

public class ComputerPlayerTurn {
	
	
	ComputerAttackMonsterLogic al;

	public void processComputerActions(ActorRef out, GameState g) {
		ComputerPlayer compPlayer = g.getPlayerTwo();
		AIUnitStateController controller = new AIUnitStateController(out, g);
		
		ArrayList<structures.basic.ComputerLogic.ComputerInstruction> cardsToPlay, monstersToMove, attacksToPerform;
		
		cardsToPlay = compPlayer.playCards();
		
		for (ComputerInstruction cI : cardsToPlay) {
			controller.summonMonster(cI.getCard(), cI.getTargetTile());
		}
		
		//process attack bf moving!!

		monstersToMove = compPlayer.moveMonsters();
		
		for (ComputerInstruction cI : cardsToPlay) {
			Tile currTile = cI.getActor().getPosition().getTile(g.getBoard());
			controller.unitMove(currTile, cI.getTargetTile());
		}
		
		
		//iterate over list and call method in controller
		
		//iterate
		
		//method to return list of monsters to perform an attack (+ move if relevant)
		
		al.smartAttack();
		
		
		
		
		//iterate
	}
}
