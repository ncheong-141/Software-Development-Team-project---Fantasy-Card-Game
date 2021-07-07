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
		g.getPlayerTwo().setGameBoard(g.getBoard());
		ComputerPlayer compPlayer = g.getPlayerTwo();
		AIUnitStateController controller = new AIUnitStateController(out, g);
		
		ArrayList<structures.basic.ComputerLogic.ComputerInstruction> cardsToPlay, monstersToMove, attacksToPerform;
		
		cardsToPlay = compPlayer.playCards();
		
		if (!cardsToPlay.isEmpty() && cardsToPlay != null) {
			for (ComputerInstruction cI : cardsToPlay) {
			//card get class 
			controller.summonMonster(cI.getCard(), cI.getTargetTile());
			//spell treated as a card 
			}
		
		}
		
		attacksToPerform = compPlayer.performAttacks();
		
		//need to double check method for attack in AI state controller

		
		monstersToMove = compPlayer.moveMonsters();
		//check if empty
		
		if (!monstersToMove.isEmpty() && monstersToMove != null) {
			for (ComputerInstruction cI : monstersToMove) {
			Tile currTile = cI.getActor().getPosition().getTile(g.getBoard());
			controller.unitMove(currTile, cI.getTargetTile());
			}
		}
		
		
		
		//iterate over list and call method in controller
		
		//iterate
		
		//method to return list of monsters to perform an attack (+ move if relevant)
		
		
		
		
		
		
		//iterate
	}
}
