package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditRoomComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
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
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PoolRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RunestoneRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StatueRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ToxicGasRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.TrapsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.TreasuryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.AquariumRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BlacksmithRoom;
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
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RitualSiteRoom;
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
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

import java.util.Locale;

public class RoomItem extends EditorItem {

    private final Room room;

    public RoomItem(Room room) {
        this.room = room;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, Messages.titleCase(getName(room.getClass())), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null) return;

                if (icon != null) remove(icon);
                icon = getSprite();
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditRoomComp(this);
    }

    @Override
    public Image getSprite() {
        return getImage(room().getClass());
    }

    @Override
    public void place(int cell) {
        //can't be placed
    }

    @Override
    public String name() {
        return getName(room().getClass());
    }

    public Room room() {
        return room;
    }

    @Override
    public Item getCopy() {
        return new RoomItem(room().getCopy());
    }

    public static String getName(Class<? extends Room> r) {
        return Messages.get(RoomItem.class, r.getSimpleName().toLowerCase(Locale.ENGLISH));
    }

    public static Image getImage(Class<? extends Room> r) {

        //standard rooms
        if (r == AquariumRoom.class)  return new PiranhaSprite();//TODO maybe make own sprite with bg included?
        if (r == BlacksmithRoom.class) return new BlacksmithSprite();
        if (r == BurnedRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.EMBERS, -1));
        if (r == CaveRoom.class) return new ItemSprite(Assets.Environment.TILES_CAVES, new TileItem(Terrain.EMPTY, -1));
        if(r == CavesFissureRoom.class) return new ItemSprite(Assets.Environment.TILES_CAVES, new TileItem(-1, DungeonTileSheet.CHASM_FLOOR, -1));
        //CellBlock
        if (r == ChasmRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(-1, DungeonTileSheet.CHASM_WALL, -1));
        if (r == CircleBasinRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(-1, DungeonTileSheet.CHASM_FLOOR_SP, -1));
        if (r == CirclePitRoom.class)return new ItemSprite(Assets.Environment.TILES_CAVES, new TileItem(-1, DungeonTileSheet.CHASM_WALL, -1));
        if (r == EntranceRoom.class)return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.ENTRANCE, -1));
        if (r == ExitRoom.class)    return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.EXIT, -1));
        if (r == FissureRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(-1, DungeonTileSheet.CHASM_FLOOR, -1));
        if (r == GrassyGraveRoom.class) return new ItemSprite(ItemSpriteSheet.TOMB);
        if (r == HallwayRoom.class) return new ItemSprite(Assets.Environment.TILES_CITY, new TileItem(Terrain.EMPTY_SP, -1));
        if (r == ImpShopRoom.class) return new ImpSprite();
        if (r == MinefieldRoom.class) return TrapItem.getTrapImage(65);//explosive trap
        //Pillars
        //Plants
        if (r == PlatformRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(-1, DungeonTileSheet.CHASM_FLOOR_SP, -1));
        //Ring
        if (r == RitualSiteRoom.class) return new ItemSprite(ItemSpriteSheet.CANDLE);
        //Ruins
        if (r == SegmentedLibraryRoom.class) return new ItemSprite(Assets.Environment.TILES_CITY, new TileItem(Terrain.BOOKSHELF, -1));
        if (r == SegmentedRoom.class) return new ItemSprite(Assets.Environment.TILES_PRISON, new TileItem(Terrain.BOOKSHELF, -1));
        if (r == SkullsRoom.class) return new ItemSprite(Assets.Environment.TILES_HALLS, new TileItem(Terrain.STATUE, -1));
        if (r == SewerPipeRoom.class) {
            return new ItemSprite(Assets.Environment.WATER_SEWERS, new TileItem(Terrain.WATER, -1));
        }
        if (r == StatuesRoom.class) return new ItemSprite(Assets.Environment.TILES_CITY, new TileItem(Terrain.STATUE_SP, -1));
        if (r == StripedRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.EMPTY_SP, -1));
        if (r == StudyRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.BOOKSHELF, -1));
        if (r == SuspiciousChestRoom.class) return new MimicSprite();

        //special rooms
        if (r == ArmoryRoom.class) return new ItemSprite(ItemSpriteSheet.ARMOR_LEATHER);
        if (r == CryptRoom.class) return new WraithSprite();
        if (r == CrystalChoiceRoom.class)  return new ItemSprite(ItemSpriteSheet.CHEST);
        if (r == CrystalVaultRoom.class) return new ItemSprite(ItemSpriteSheet.CRYSTAL_CHEST);
        if (r == CrystalPathRoom.class) return new ItemSprite(ItemSpriteSheet.CRYSTAL_KEY);
        if (r == DemonSpawnerRoom.class) return new SpawnerSprite();
        if (r == GardenRoom.class) return new ItemSprite(ItemSpriteSheet.SEED_SUNGRASS);
        if (r == LaboratoryRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.ALCHEMY, -1));
        if (r == LibraryRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.BOOKSHELF, -1));
        //MagicalFire
        if (r == MagicWellRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.WELL, -1));
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
        //storage
        if (r == ToxicGasRoom.class) return TrapItem.getTrapImage(40);//toxic vent
        if (r == TrapsRoom.class) return TrapItem.getTrapImage(7);
        if (r == TreasuryRoom.class) return new ItemSprite(ItemSpriteSheet.GOLD);
        if (r == WeakFloorRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(-1, DungeonTileSheet.CHASM_FLOOR_SP, -1));

        //secret rooms
        if (r == SecretArtilleryRoom.class) return new ItemSprite(ItemSpriteSheet.KUNAI);
        if (r == SecretChestChasmRoom.class) return new ItemSprite(ItemSpriteSheet.LOCKED_CHEST);
        if (r == SecretGardenRoom.class) return new ItemSprite(ItemSpriteSheet.SEED_SUNGRASS);
        if (r == SecretHoardRoom.class) return TrapItem.getTrapImage(83);//Poison dart trap
        if (r == SecretHoneypotRoom.class) return new ItemSprite(ItemSpriteSheet.HONEYPOT);
        if (r == SecretLaboratoryRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.ALCHEMY, -1));
        if (r == SecretLarderRoom.class) return new ItemSprite(ItemSpriteSheet.PASTY);
        if (r == SecretLibraryRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.BOOKSHELF, -1));
        //secretMaze
        if (r == SecretRunestoneRoom.class) return new ItemSprite(ItemSpriteSheet.STONE_ENCHANT);
        if (r == SecretSummoningRoom.class) return TrapItem.getTrapImage(20);//Summoning trap
        if (r == SecretWellRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.WELL, -1));


        if (r == EmptyRoom.class) return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.EMPTY, -1));

        Class<?> superclass = r.getSuperclass();
        if (Room.class.isAssignableFrom(superclass))
            return getImage((Class<? extends Room>) superclass);
        return new ItemSprite(ItemSpriteSheet.SOMETHING);
    }
}