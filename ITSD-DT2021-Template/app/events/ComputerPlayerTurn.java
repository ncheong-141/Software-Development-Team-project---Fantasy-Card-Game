package events;



import akka.actor.ActorRef;
import java.util.ArrayList;
import structures.GameState;
import events.gameplaystates.unitplaystates.AIUnitStateController;
import structures.basic.ComputerLogic.*;
import structures.basic.ComputerPlayer;
import structures.basic.Player;
import structures.basic.Tile;

public class ComputerPlayerTurn {
	
	
	

	public void processComputerActions(ActorRef out, GameState g) {
		ComputerPlayer pl2 = (ComputerPlayer) g.getPlayerTwo();
		pl2.setGameBoard(g.getBoard());
		ComputerPlayer compPlayer = pl2;
		AIUnitStateController controller = new AIUnitStateController(out, g);
		compPlayer.setMana(9);
		compPlayer.setHPBenchMark(10);
		
		ArrayList<structures.basic.ComputerLogic.ComputerInstruction> cardsToPlay, monstersToMove, attacksToPerform;
		
		System.out.println("=====================AI turn: computing cards=======================");
		cardsToPlay = compPlayer.playCards();
		
		if (!cardsToPlay.isEmpty() && cardsToPlay != null) {
			
			for (ComputerInstruction cI : cardsToPlay) {
				System.out.println("I want to play this card: " + cI.getCard().getCardname() + " on this tile: " + cI.getTargetTile());
				System.out.println("-----------");
			//card get class 
			//controller.summonMonster(cI.getCard(), cI.getTargetTile());
			//spell treated as a card 
			}
		
		}
		
		System.out.println("=====================AI turn: computing attacks=======================");
		attacksToPerform = compPlayer.performAttacks();
		
		if (attacksToPerform != null && !attacksToPerform.isEmpty()) {
			System.out.println("Attacks: ");
			for (ComputerInstruction cI : attacksToPerform) {
				System.out.println(cI.getActor().getName()+ " attacks to tile ( " + cI.getTargetTile().getTilex() + " "
						+ cI.getTargetTile().getTiley()+ " )");
			}
		}
		
		else {
			System.out.println("no attacks to perform");
		}
		
		//need to double check method for attack in AI state controller

		
		monstersToMove = compPlayer.moveMonsters();
		//check if empty
		
		if (!monstersToMove.isEmpty() && monstersToMove != null) {
			for (ComputerInstruction cI : monstersToMove) {
				System.out.println("move monster: " + cI.getActor().getName() + " to tile: " + cI.getTargetTile());
			Tile currTile = cI.getActor().getPosition().getTile(g.getBoard());
			//controller.unitMove(currTile, cI.getTargetTile());
			}
		}
		else System.out.println("no moves to make");
		
		
		
		//iterate over list and call method in controller
		
		//iterate
		
		//method to return list of monsters to perform an attack (+ move if relevant)
		
		
		
		
		
		
		//iterate
	}
}
