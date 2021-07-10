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
		
		
		g.getComputerAvatar().setMovesLeft(2);
		g.getComputerAvatar().setName("bob");
		
		
		
		ArrayList<structures.basic.ComputerLogic.ComputerInstruction> cardsToPlay, monstersToMove, attacksToPerform;
		
		System.out.println("=====================AI turn: computing cards=======================");
		cardsToPlay = compPlayer.playCards();
		
		System.out.println("=====================AI turn: computing attacks=======================");
		attacksToPerform = compPlayer.performAttacks();
		
		
		System.out.println("=======================AI turn: computing moves=========================");
		monstersToMove = compPlayer.moveMonsters();
		
		if (!cardsToPlay.isEmpty() && cardsToPlay != null) {
			
			for (ComputerInstruction cI : cardsToPlay) {
				System.out.println("I want to play this card: " + cI.getCard().getCardname() + " on this tile: " + cI.getTargetTile());
				System.out.println("-----------");
			//card get class 
			controller.summonMonster(cI.getCard(), cI.getTargetTile());
			 
			}
		
		}
		
		
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
		
		

		
		
		//check if empty
		
		if (!monstersToMove.isEmpty() && monstersToMove != null) {
			for (ComputerInstruction cI : monstersToMove) {
				if (cI.getActor() == null) System.out.println("monster is null");
				if (cI.getTargetTile() == null) System.out.println("tile is null");
				System.out.println("move monster: " + cI.getActor().getName() + " to tile: " + cI.getTargetTile());
			Tile currTile = cI.getActor().getPosition().getTile(g.getBoard());
			//controller.unitMove(currTile, cI.getTargetTile());
			}
		}
		else System.out.println("no moves to make");
		
		
		g.computerEnd();
		
		

	}
}
