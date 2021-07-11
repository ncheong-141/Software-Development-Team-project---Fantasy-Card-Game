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

/**
 * 
 * @author Chiara Pascucci and Yufen Chen
 * this class receives the instructions that the computer player wants to perform
 * and uses the AI Unit State Controller class to update the UI to display those actions
 *
 */

public class ComputerPlayerTurn {
	
	
	

	public void processComputerActions(ActorRef out, GameState g) {
		
		ComputerPlayer compPlayer = (ComputerPlayer) g.getPlayerTwo();
		
		AIUnitStateController controller = new AIUnitStateController(out, g);
	
		compPlayer.setHPBenchMark(10);
		
		
		g.getComputerAvatar().setMovesLeft(2);
		g.getComputerAvatar().setName("bob");
		
		
		
		ArrayList<structures.basic.ComputerLogic.ComputerInstruction> cardsToPlay, monstersToMove, attacksToPerform;
		
		
		//generating the list of cards that the ComputerPlayer wants to play + their target tiles
		cardsToPlay = compPlayer.playCards(g.getBoard());
		
		if (!cardsToPlay.isEmpty() && cardsToPlay != null) {
			
			for (ComputerInstruction cI : cardsToPlay) {
				
				  if (cI.getCard() == null || cI.getTargetTile() == null) continue; 
				  else { 
					 
					  if  (cI.getCard().getAssociatedClass() == Spell.class) controller.spellCast(cI.getCard(), cI.getTargetTile()); 
					  else { controller.summonMonster(cI.getCard(), cI.getTargetTile());
				  try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();} } }
				 

			}
		
		}
		

		//generating the list of monsters that the computer player wants to attack with + their targets
		attacksToPerform = compPlayer.performAttacks(g.getBoard());
		
		if (attacksToPerform != null && !attacksToPerform.isEmpty()) {
			
			for (ComputerInstruction cI : attacksToPerform) {
				
				if (cI.getActor() == null || cI.getTargetTile() == null) continue;
				
				Tile currTile = g.getBoard().getTile(cI.getActor().getPosition().getTilex(), cI.getActor().getPosition().getTiley());
				controller.unitAttack(currTile, cI.getTargetTile());
				try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		
		else {
			System.out.println("no attacks to perform");
		}
		
		
		//generating the list of friendly unit that the computer player wants to move + their target tiles
		monstersToMove = compPlayer.moveMonsters(g.getBoard());
		
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
		
		//ending computer player's turn
		g.computerEnd();
		
		

	}
}
