package structures.basic.abilities;

/** Specific call IDs which define when an ability should be activated. 
 * Each ability has one of these to flag the game when to activate **/

public enum Call_IDs {

	noTimeConstraint, construction, onSummon,onCardClicked, onDeath, onEnemySpellCast, onUnitSelection, onFriendlyAvatarDamageTaken, onSelectingACard;
}
