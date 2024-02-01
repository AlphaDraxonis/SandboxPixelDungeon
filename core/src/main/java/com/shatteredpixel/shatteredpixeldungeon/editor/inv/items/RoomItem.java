package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditRoomComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.RatKingRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretArtilleryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretChestChasmRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretHoardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretHoneypotRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretLaboratoryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretLarderRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretLibraryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRunestoneRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretSummoningRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretWellRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ArmoryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.CryptRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.CrystalChoiceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.CrystalPathRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.CrystalVaultRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.DemonSpawnerRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.GardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.LaboratoryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.LibraryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicWellRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PoolRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RunestoneRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StatueRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ToxicGasRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.TrapsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.TreasuryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.AquariumRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BurnedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CavesFissureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ChasmRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CircleBasinRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CirclePitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.FissureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.GrassyGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.HallwayRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.MinefieldRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PlatformRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SegmentedLibraryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SegmentedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SewerPipeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SkullsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StatuesRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StripedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StudyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SuspiciousChestRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PiranhaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatKingSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShopkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpawnerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

import java.util.Locale;

public class RoomItem extends EditorItem<Room> {

    public RoomItem(){}
    public RoomItem(Room room) {
        this.obj = room;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditRoomComp(this);
    }

    @Override
    public String name() {
        return getName(getObject().getClass());
    }

    @Override
    public Image getSprite() {
        return getImage(getObject().getClass());
    }

    @Override
    public void place(int cell) {
        //can't be placed
    }

    @Override
    public Item getCopy() {
        return new RoomItem(getObject().getCopy());
    }

    public static String getName(Class<? extends Room> r) {
        return Messages.get(RoomItem.class, r.getSimpleName().toLowerCase(Locale.ENGLISH));
    }

    public static String getDesc(Class<? extends Room> r) {
        return Messages.get(RoomItem.class, r.getSimpleName().toLowerCase(Locale.ENGLISH) + "_desc");
    }

    public static Image getImage(Class<? extends Room> r) {

        //standard rooms
        if (r == AquariumRoom.class)  return new PiranhaSprite();//TODO maybe make own sprite with bg included?
        if (r == BlacksmithRoom.class) return new BlacksmithSprite();
        if (r == BurnedRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.EMBERS);
        if (r == CaveRoom.class) return new TileSprite(Assets.Environment.TILES_CAVES, Terrain.EMPTY);
        if(r == CavesFissureRoom.class) return TileSprite.createTilespriteWithImage(Assets.Environment.TILES_CAVES, DungeonTileSheet.CHASM_FLOOR);
        //CellBlock
        if (r == ChasmRoom.class) return TileSprite.createTilespriteWithImage(Assets.Environment.TILES_SEWERS, DungeonTileSheet.CHASM_WALL);
        if (r == CircleBasinRoom.class) return TileSprite.createTilespriteWithImage(Assets.Environment.TILES_SEWERS,DungeonTileSheet.CHASM_FLOOR_SP);
        if (r == CirclePitRoom.class)return TileSprite.createTilespriteWithImage(Assets.Environment.TILES_CAVES, DungeonTileSheet.CHASM_WALL);
        if (r == EntranceRoom.class)return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.ENTRANCE);
        if (r == ExitRoom.class)    return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.EXIT);
        if (r == FissureRoom.class) return TileSprite.createTilespriteWithImage(Assets.Environment.TILES_SEWERS, DungeonTileSheet.CHASM_FLOOR);
        if (r == GrassyGraveRoom.class) return new ItemSprite(ItemSpriteSheet.TOMB);
        if (r == HallwayRoom.class) return new TileSprite(Assets.Environment.TILES_CITY, Terrain.EMPTY_SP);
        if (r == ImpShopRoom.class) return new ImpSprite();
        if (r == MinefieldRoom.class) return EditorUtilies.getTerrainFeatureTexture(65);//explosive trap
        //Pillars
        //Plants
        if (r == PlatformRoom.class) return TileSprite.createTilespriteWithImage(Assets.Environment.TILES_SEWERS, DungeonTileSheet.CHASM_FLOOR_SP);
        //Ring
        if (r == RitualSiteRoom.class) return new ItemSprite(ItemSpriteSheet.CANDLE);
        //Ruins
        if (r == SegmentedLibraryRoom.class) return new TileSprite(Assets.Environment.TILES_CITY, Terrain.BOOKSHELF);
        if (r == SegmentedRoom.class) return new TileSprite(Assets.Environment.TILES_PRISON, Terrain.BOOKSHELF);
        if (r == SkullsRoom.class) return new TileSprite(Assets.Environment.TILES_HALLS, Terrain.STATUE);
        if (r == SewerPipeRoom.class)   return new TileSprite(Assets.Environment.WATER_SEWERS, Terrain.WATER);
        if (r == StatuesRoom.class) return new TileSprite(Assets.Environment.TILES_CITY, Terrain.STATUE_SP);
        if (r == StripedRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.EMPTY_SP);
        if (r == StudyRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.BOOKSHELF);
        if (r == SuspiciousChestRoom.class) return new MimicSprite();

        //special rooms
        if (r == ArmoryRoom.class) return new ItemSprite(ItemSpriteSheet.ARMOR_LEATHER);
        if (r == CryptRoom.class) return new WraithSprite();
        if (r == CrystalChoiceRoom.class)  return new ItemSprite(ItemSpriteSheet.CHEST);
        if (r == CrystalVaultRoom.class) return new ItemSprite(ItemSpriteSheet.CRYSTAL_CHEST);
        if (r == CrystalPathRoom.class) return new ItemSprite(ItemSpriteSheet.CRYSTAL_KEY);
        if (r == DemonSpawnerRoom.class) return new SpawnerSprite();
        if (r == GardenRoom.class) return new ItemSprite(ItemSpriteSheet.SEED_SUNGRASS);
        if (r == LaboratoryRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.ALCHEMY);
        if (r == LibraryRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.BOOKSHELF);
        //MagicalFire
        if (r == MagicWellRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.WELL);
        if (r == MassGraveRoom.class) return new ItemSprite(ItemSpriteSheet.TOMB);
        if (r == PitRoom.class) return new ItemSprite(ItemSpriteSheet.BONES);
        if (r == PoolRoom.class)  return new PiranhaSprite();//TODO maybe make own sprite with bg included?
        if (r == RatKingRoom.class) return new RatKingSprite();
        if (r == RotGardenRoom.class) return new ItemSprite(ItemSpriteSheet.SEED_ROTBERRY);
        if (r == RunestoneRoom.class) return new ItemSprite(ItemSpriteSheet.STONE_ENCHANT);
        //SacrificeRoom
        if (r == SentryRoom.class) return new SentryRoom.SentrySprite();
        if (r == ShopRoom.class) return new ShopkeeperSprite();
        if (r == StatueRoom.class) return new StatueSprite();
        //Storage
        if (r == ToxicGasRoom.class) return EditorUtilies.getTerrainFeatureTexture(40);//toxic vent
        if (r == TrapsRoom.class) return EditorUtilies.getTerrainFeatureTexture(7);
        if (r == TreasuryRoom.class) return new ItemSprite(ItemSpriteSheet.GOLD);
        if (r == WeakFloorRoom.class) return TileSprite.createTilespriteWithImage(Assets.Environment.TILES_SEWERS, DungeonTileSheet.CHASM_FLOOR_SP);

        //secret rooms
        if (r == SecretArtilleryRoom.class) return new ItemSprite(ItemSpriteSheet.KUNAI);
        if (r == SecretChestChasmRoom.class) return new ItemSprite(ItemSpriteSheet.LOCKED_CHEST);
        if (r == SecretGardenRoom.class) return new ItemSprite(ItemSpriteSheet.SEED_SUNGRASS);
        if (r == SecretHoardRoom.class) return EditorUtilies.getTerrainFeatureTexture(83);//Poison dart trap
        if (r == SecretHoneypotRoom.class) return new ItemSprite(ItemSpriteSheet.HONEYPOT);
        if (r == SecretLaboratoryRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.ALCHEMY);
        if (r == SecretLarderRoom.class) return new ItemSprite(ItemSpriteSheet.PASTY);
        if (r == SecretLibraryRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.BOOKSHELF);
        //SecretMaze
        if (r == SecretRunestoneRoom.class) return new ItemSprite(ItemSpriteSheet.STONE_ENCHANT);
        if (r == SecretSummoningRoom.class) return EditorUtilies.getTerrainFeatureTexture(20);//Summoning trap
        if (r == SecretWellRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.WELL);


        if (r == EmptyRoom.class) return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.EMPTY);

        Class<?> superclass = r.getSuperclass();
        if (Room.class.isAssignableFrom(superclass))
            return getImage((Class<? extends Room>) superclass);
        return new ItemSprite(ItemSpriteSheet.SOMETHING);
    }

    private static final String ROOM = "room";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ROOM, obj);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        obj = (Room) bundle.get(ROOM);
    }

    public Room room() {
        return getObject();
    }
}