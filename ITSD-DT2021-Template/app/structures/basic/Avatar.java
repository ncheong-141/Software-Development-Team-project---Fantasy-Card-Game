package structures.basic;

import java.util.ArrayList;
//=============================Class description =============================//
//this represent the avatar that both the human player and computer player will use to play the game
//this class stores a reference to the board where the avatar are placed (at specific positions)
//this class stores a reference to the owner of the avatar, human or computer player
//==========================================================================//

public class Avatar extends Monster {
	Board board;
	
	public Avatar() {}
	
	public Avatar(Board b) {
		super();
		
		this.board = b;
	}


	private void setPositionByPlayer () {
		if (this.owner instanceof HumanPlayer) {
			this.setPositionByTile(board.getTile(2,1));
			board.getTile(2, 1).addUnit(this);
		}
		
		else if (this.owner instanceof ComputerPlayer) {
			this.setPositionByTile(board.getTile(2,7));
			board.getTile(2, 7).addUnit(this);
		}
	}

	
}
