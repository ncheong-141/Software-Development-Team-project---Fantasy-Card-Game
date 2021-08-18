package structures.basic;

/**
 * 
 * @author Chiara Pascucci
 *	this class represent the avatar that both the human player and computer player will use to play the game
	this class stores a reference to the board where the avatar are placed (at specific positions)
	this class stores a reference to the owner of the avatar, human or computer player
 */

public class Avatar extends Monster {
	
	// Constructor
	public Avatar() {
		super();
		this.avatarSetUp();
	}

	// Setting up all stats for avatar
	public void avatarSetUp() {
		
		// Initialise avatar with these stats
		this.HP = 20;
		this.maxHP = 20;
		this.attackValue = 2;
		this.movesLeft = 2;			
		this.attacksLeft = 1;		
		this.attacksMax = 1;		
		this.attackRange = 1;		
		this.onCooldown = false;
	}
	
	public void setOwner(Player p) {
		this.owner = p;
	}

	// Overide monster defend
	@Override
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
	
	// Override monster heal
	@Override
	public boolean heal(int h) {
		if(this.HP == this.maxHP)	{	return false;	}	
		if(this.HP + h > this.maxHP) {
			this.HP = this.maxHP;
			this.getOwner().setHealth(h);
		}
		else {
			this.HP += h;
			this.getOwner().setHealth(h);
		}
		return true;
	}
	
}
