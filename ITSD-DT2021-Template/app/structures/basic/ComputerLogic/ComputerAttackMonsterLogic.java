package structures.basic.ComputerLogic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import structures.basic.*;

/**
 * 
 * @author Chiara Pascucci and Yufen Chen
 * This class holds the logic that the computer player uses to decide what attacks to perfom
 * this class is instantiated within computer player which calls its public method
 * the private methods in the class are used to compute the best attacks to perform, those are then returned to computer player
 *
 */
public class ComputerAttackMonsterLogic {
	
	private ComputerPlayer player;

	public ComputerAttackMonsterLogic(ComputerPlayer p) {
		this.player = p;
	}
	
	/**
	 * 
	 * @param gameBoard
	 * @return list of computer instruction objects representing the attacks that the computer player wants to perform
	 * method uses the private methods in this class in sequence to compute best attack moves
	 */
	public ArrayList <ComputerInstruction> computerAttacks(Board gameBoard){
		ArrayList <ComputerInstruction> list = new ArrayList<ComputerInstruction>();
		
		ArrayList <Monster> monstersThatCanAttack = this.monstersThatCanAttack(gameBoard);
		//System.out.println("I have " + monstersThatCanAttack.size() + " that can attack");
		
		if (monstersThatCanAttack.isEmpty()) return list;
		
		ArrayList<MonsterTargetOtpion> monstersAndTheirListOfTargets = this.getMonstersPossTargets(monstersThatCanAttack, gameBoard);
		
		list = this.matchMonsterAndTarget(monstersAndTheirListOfTargets);
		
		return list;
		
	}
	
	/**
	 * 1.
	 * @param gameBoard
	 * @return list of all monsters that belong to the specified player and that can attack during current turn
	 */
	private ArrayList<Monster> monstersThatCanAttack(Board gameBoard){
		ArrayList <Monster> list = gameBoard.friendlyUnitList(player);
		list.removeIf(m->(m.getAttacksLeft() <=0 || m.getOnCooldown()));
		return list;		
	}
	
	/**
	 * 2.
	 * @param monstersThatCanAttackList (list of all monsters that can attack)
	 * @param b (Board object)
	 * @return list of MonsterTargetOption objects (each object contains a monster that can attack and a list of possible targets for that monster)
	 * 
	 */
	private ArrayList<MonsterTargetOtpion> getMonstersPossTargets(ArrayList<Monster> monstersThatCanAttackList, Board b){

		if(monstersThatCanAttackList.isEmpty()) return null;
		
		//System.out.println("=== calculating where my monsters can attack ====");
		
		ArrayList<MonsterTargetOtpion> listOne = new ArrayList<MonsterTargetOtpion>();
		//MonsterTargetOtpion[] list = new MonsterTargetOtpion[monstersThatCanAttackList.size()];

		for (Monster m : monstersThatCanAttackList) {
			
			//System.out.println("looking at possible targets for monster : " +m.getName());
			MonsterTargetOtpion attackTargets = new MonsterTargetOtpion(m, b);
	
				listOne.add(attackTargets);
		
		}
		
		return listOne;
	}
	
	/**
	 * 3.
	 * @param targOptsList (list of MonsterTargetOption objects)
	 * @return list of computer instructions that should be executed. 
	 * Each ComputerInstruction object will contain a monster reference (monster to perform the attack)
	 * and a tile reference containing the target
	 */
	private ArrayList<ComputerInstruction> matchMonsterAndTarget(ArrayList<MonsterTargetOtpion> targOptsList){
		ArrayList<ComputerInstruction> list = new ArrayList<ComputerInstruction>();
		
		if (targOptsList == null) return list;
		
		//System.out.println("[line 79] size of targ opts list bf filter: " + targOptsList.size());
		
		targOptsList.removeIf(trg -> (trg.getScore() < 0 || trg == null));

		//System.out.println("[line 83] size of targ opts list af filter: " + targOptsList.size());
		
		Collections.sort(targOptsList);

		HashSet <Tile> targets = new HashSet<Tile>();
		
		int k = 0;
		for (MonsterTargetOtpion mto : targOptsList) {
			
			Tile targTile = mto.getList().get(k);
			
			if (!targets.contains(targTile)) {
				ComputerInstruction inst = new ComputerInstruction (mto.getM(), targTile);
				list.add(inst);
				targets.add(targTile);
				continue;
			}
			
			else {
				//System.out.println("len of tile options for monster is " + mto.getList().size() );
				if (mto.getList().size() <= k+1) continue;
				do {
					
					k++;
					targTile = mto.getList().get(k);
					
				}while(targets.contains(targTile) && k+1 < mto.getList().size());
				
				if (!targets.contains(targTile)) {
					ComputerInstruction inst = new ComputerInstruction (mto.getM(), targTile);
					list.add(inst);
					targets.add(targTile);
				}
				
				k=0;
			}
			
		}
		
		return list;
	}

}
