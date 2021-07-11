package events;



import akka.actor.ActorRef;
import java.util.ArrayList;
import structures.GameState;
import events.gameplaystates.unitplaystates.AIUnitStateController;
import structures.basic.ComputerLogic.*;
import structures.basic.ComputerPlayer;
import structures.basic.Player;
import structures.basic.Spell;
import structures.basic.Tile;

public class ComputerPlayerTurn {
	
	
	

	public void processComputerActions(ActorRef out, GameState g) {
		
		ComputerPlayer pl2 = (ComputerPlayer) g.getPlayerTwo();
		ComputerPlayer compPlayer = pl2;
		AIUnitStateController controller = new AIUnitStateController(out, g);
		compPlayer.setHPBenchMark(10);
		
		
		g.getComputerAvatar().setName("bob");
		
		
		
		ArrayList<structures.basic.ComputerLogic.ComputerInstruction> cardsToPlay, monstersToMove, attacksToPerform;
		
		System.out.println("=====================AI turn: computing cards=======================");
		cardsToPlay = compPlayer.playCards(g.getBoard());
		
		if (!cardsToPlay.isEmpty() && cardsToPlay != null) {
			
			for (ComputerInstruction cI : cardsToPlay) {
				System.out.println(cI);
				
				  if (cI.getCard() == null || cI.getTargetTile() == null) continue; 
				  else { 
					  System.out.println("get class: " + cI.getCard().getClass().getName());
					  System.out.println("get associated class: " + cI.getCard().getAssociatedClass().getName());
					  if  (cI.getCard().getAssociatedClass() == Spell.class) controller.spellCast(cI.getCard(), cI.getTargetTile()); 
					  else { controller.summonMonster(cI.getCard(), cI.getTargetTile());
				  try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();} } }
				 

			}
		
		}
		
		System.out.println("=====================AI turn: computing attacks=======================");
		attacksToPerform = compPlayer.performAttacks(g.getBoard());
		
		if (attacksToPerform != null && !attacksToPerform.isEmpty()) {
			System.out.println("Attacks: ");
			for (ComputerInstruction cI : attacksToPerform) {
				System.out.println(cI);
				if (cI.getActor() == null || cI.getTargetTile() == null) continue;
				
				Tile currTile = g.getBoard().getTile(cI.getActor().getPosition().getTilex(), cI.getActor().getPosition().getTiley());
				controller.unitAttack(currTile, cI.getTargetTile());
				try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		
		else {
			System.out.println("no attacks to perform");
		}
		
		
		System.out.println("=======================AI turn: computing moves=========================");
		monstersToMove = compPlayer.moveMonsters(g.getBoard());

		//check if empty
		
		if (!monstersToMove.isEmpty() && monstersToMove != null) {
			for (ComputerInstruction cI : monstersToMove) {
				System.out.println(cI);
				if (cI.getActor() == null || cI.getTargetTile() == null) continue;
				
			Tile currTile = cI.getActor().getPosition().getTile(g.getBoard());
			controller.unitMove(currTile, cI.getTargetTile());
			try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		else System.out.println("no moves to make");
		
		
		g.computerEnd();
		
		

	}
}
