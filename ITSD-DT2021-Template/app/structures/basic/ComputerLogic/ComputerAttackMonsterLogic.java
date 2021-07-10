package structures.basic.ComputerLogic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import structures.GameState;
import structures.basic.*;
import structures.basic.ComputerLogic.ComputerMoveMonsterLogic.MonsterTileOption;
public class ComputerAttackMonsterLogic {
	
	private ComputerPlayer player;
	private ArrayList<Tile> attackerTiles; // all tiles holds all ComputerPlayer monster
	private ArrayList<Tile> targetInRange;  //a list of attackable enemy for each attacker
	private Tile target; //attack target

	public ComputerAttackMonsterLogic(ComputerPlayer p) {
		this.player = p;
	}
	
	public ArrayList <ComputerInstruction> computerAttacks(Board gameBoard){
		ArrayList <ComputerInstruction> list = new ArrayList<ComputerInstruction>();
		
		ArrayList <Monster> monstersThatCanAttack = this.monstersThatCanAttack(gameBoard);
		//System.out.println("I have " + monstersThatCanAttack.size() + " that can attack");
		
		if (monstersThatCanAttack.isEmpty()) return list;
		
		ArrayList<MonsterTargetOtpion> monstersAndTheirListOfTargets = this.getMonstersPossTargets(monstersThatCanAttack, gameBoard);
		
		list = this.matchMonsterAndTarget(monstersAndTheirListOfTargets);
		
		return list;
		
	}
	
	private ArrayList<Monster> monstersThatCanAttack(Board gameBoard){
		ArrayList <Monster> list = gameBoard.friendlyUnitList(player);
		list.removeIf(m->(m.getAttacksLeft() <=0 || m.getOnCooldown()));
		return list;		
	}
	
	private ArrayList<MonsterTargetOtpion> getMonstersPossTargets(ArrayList<Monster> monstersThatCanAttackList, Board b){
		
		
		if(monstersThatCanAttackList.isEmpty()) return null;
		
		//System.out.println("=== calculating where my monsters can attack ====");
		
		ArrayList<MonsterTargetOtpion> listOne = new ArrayList<MonsterTargetOtpion>();
		//MonsterTargetOtpion[] list = new MonsterTargetOtpion[monstersThatCanAttackList.size()];
		
		int i = 0;
		for (Monster m : monstersThatCanAttackList) {
			
			//System.out.println("looking at possible targets for monster : " +m.getName());
			MonsterTargetOtpion attackTargets = new MonsterTargetOtpion(m, b);
	
				listOne.add(attackTargets);
		
		}
		
		return listOne;
	}
	
	private ArrayList<ComputerInstruction> matchMonsterAndTarget(ArrayList<MonsterTargetOtpion> targOptsList){
		ArrayList<ComputerInstruction> list = new ArrayList<ComputerInstruction>();
		
		if (targOptsList == null) return list;
		
		//System.out.println("[line 79] size of targ opts list bf filter: " + targOptsList.size());
		
		targOptsList.removeIf(trg -> (trg.getScore() < 0 || trg == null));
		
		for (MonsterTargetOtpion mto : targOptsList) {
			//System.out.println(mto);
		}
		
		
		//System.out.println("[line 83] size of targ opts list af filter: " + targOptsList.size());
		
		
		Collections.sort(targOptsList);
		
		//System.out.println("list after sorting");
		
		for (MonsterTargetOtpion mto : targOptsList) {
			//System.out.println(mto);
		}
		
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
