package structures.basic;

import java.util.ArrayList;
//=============================Class description =============================//
//this represent the avatar that both the human player and computer player will use to play the game
//this class stores a reference to the board where the avatar are placed (at specific positions)
//this class stores a reference to the owner of the avatar, human or computer player
//==========================================================================//

public class Avatar extends Monster {

	
	public Avatar() {}

	
	//when the owner is set for an avatar, this method also takes care of assigning the avatar to the relevant 
	//starting position, which is based on the type of owner
	public void setOwner(Player p, Board b) {
		this.owner = p;
		this.HP = p.getHealth();
		this.setPositionByPlayer(b);
	}

	//this method is used by the method above to check if the avatar's owner is a human or computer player
	//depending on the type the the right starting position is assigned to the avatar
	private void setPositionByPlayer (Board b) {
		if (this.owner instanceof HumanPlayer) {
			this.setPositionByTile(b.getTile(1,2));
			b.getTile(1, 2).addUnit(this);
		}
		
		else if (this.owner instanceof ComputerPlayer) {
			this.setPositionByTile(b.getTile(7,2));
			b.getTile(7,2).addUnit(this);
		}
	}

	
}
