package events;



import akka.actor.ActorRef;
import commands.GeneralCommandSets;

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

	// Attribute
	Thread AIthread = new Thread(); 

	public ComputerPlayerTurn(ActorRef out, GameState g) {

		this.AIthread = new Thread(new RunComputerTurnOnThread(out, g)); 
	}
	
	
	public void processComputerActions() {

		/** Run AI on thread to allow for waiting for executions to finish **/
		AIthread.start();
	}


	/** Inner class **/
	public static class RunComputerTurnOnThread implements Runnable {

		// Attributes
		GameState g; 
		ActorRef out;

		// Constructor 
		public RunComputerTurnOnThread(ActorRef out, GameState gameState) {
			this.g = gameState;
			this.out = out;
		}

		// Thread run
		public void run() {

			ComputerPlayer pl2 = (ComputerPlayer) g.getPlayerTwo();
			ComputerPlayer compPlayer = pl2;
			AIUnitStateController controller = new AIUnitStateController(out, g);
			compPlayer.setHPBenchMark(10);

			
			g.getComputerAvatar().setName("bob");


			ArrayList<structures.basic.ComputerLogic.ComputerInstruction> cardsToPlay, monstersToMove, attacksToPerform;

			
			cardsToPlay = compPlayer.playCards(g.getBoard());

			if (!cardsToPlay.isEmpty() && cardsToPlay != null) {

				for (ComputerInstruction cI : cardsToPlay) {
					System.out.println(cI);

					if (cI.getCard() == null || cI.getTargetTile() == null) continue; 
					else { 
						System.out.println("get class: " + cI.getCard().getClass().getName());
						System.out.println("get associated class: " + cI.getCard().getAssociatedClass().getName());
						if  (cI.getCard().getAssociatedClass() == Spell.class) controller.spellCast(cI.getCard(), cI.getTargetTile()); 
						else { 
							controller.summonMonster(cI.getCard(), cI.getTargetTile());

							// Wait between action types
							waitForActionsToComplete();
						}
					}
				}

				// Wait between action types
				waitForActionsToComplete();

				
				attacksToPerform = compPlayer.performAttacks(g.getBoard());

				if (attacksToPerform != null && !attacksToPerform.isEmpty()) {
					System.out.println("Attacks: ");
					for (ComputerInstruction cI : attacksToPerform) {
						System.out.println(cI);
						if (cI.getActor() == null || cI.getTargetTile() == null) continue;

						Tile currTile = g.getBoard().getTile(cI.getActor().getPosition().getTilex(), cI.getActor().getPosition().getTiley());
						controller.unitAttack(currTile, cI.getTargetTile());

						// Wait between action types
						waitForActionsToComplete();
					}
				}

				else {
					System.out.println("no attacks to perform");
				}


				// Wait between action types
				waitForActionsToComplete();

				monstersToMove = compPlayer.moveMonsters(g.getBoard());

				//check if empty

				if (!monstersToMove.isEmpty() && monstersToMove != null) {
					for (ComputerInstruction cI : monstersToMove) {
						
						if (cI.getActor() == null || cI.getTargetTile() == null) continue;

						Tile currTile = cI.getActor().getPosition().getTile(g.getBoard());
						controller.unitMove(currTile, cI.getTargetTile());

						// Wait between action types
						waitForActionsToComplete();
					}
				}
				else System.out.println("no moves to make");

				// Wait between action types
				waitForActionsToComplete();

				g.computerEnd();
			}

		}

		/** Helper methods **/
		public void waitForActionsToComplete() {

			// Wait between action types
			while (g.getUnitMovingFlag() || g.userinteractionLocked()) {
				GeneralCommandSets.threadSleepLong();
			}
		}

	}
}
