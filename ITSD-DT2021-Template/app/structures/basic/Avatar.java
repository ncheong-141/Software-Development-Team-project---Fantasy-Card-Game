package structures.basic;


//=============================Class description =============================//
//this represent the avatar that both the human player and computer player will use to play the game
//this class stores a reference to the board where the avatar are placed (at specific positions)
//this class stores a reference to the owner of the avatar, human or computer player
//==========================================================================//

public class Avatar extends Monster {
	public Avatar() {
		
	}
	


	public void avatarSetUp() {
		this.HP = 20;
		this.maxHP = 20;
		this.attackValue = 2;
		this.movesLeft = 2;			
		this.attacksLeft = 1;		
		this.attacksMax = 1;		
		this.attackRange = 1;		
		this.onCooldown = false;
		//this.setOwner(p);
	}
	
	//when the owner is set for an avatar, this method also takes care of assigning the avatar to the relevant 
	//starting position, which is based on the type of owner
	public void setOwner(Player p) {
		this.owner = p;
		//this.HP = p.getHealth();
	}

	
	public boolean defend(int d) {
		if(this.HP - d <= 0) {
			this.HP = 0;
			this.getOwner().setHealth(this.HP);
			return false;
		} else {
			this.HP -= d;
			this.getOwner().setHealth(this.HP);
			return true;
		}
	}
	
	

	// To do: 
	// Override defend method to adjust Player health as well
	// Delete setPositionByPlayer (+ call in setOwner), and implement manually in Initalize
	
}
