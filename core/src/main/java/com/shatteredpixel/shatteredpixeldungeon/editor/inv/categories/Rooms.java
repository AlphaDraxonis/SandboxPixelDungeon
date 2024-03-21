package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.RoomItem;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public enum Rooms {

    SEWER,
    PRISON,
    CAVES,
    CITY,
    HALLS,
    STANDARD,
    SPECIAL,
    SECRET;

    public String getName() {
        switch (this) {
            case SEWER:
                return Document.INTROS.pageTitle("Sewers");
            case PRISON:
                return Document.INTROS.pageTitle("Prison");
            case CAVES:
                return Document.INTROS.pageTitle("Caves");
            case CITY:
                return Document.INTROS.pageTitle("City");
            case HALLS:
                return Document.INTROS.pageTitle("Halls");
            case STANDARD:
                return Messages.get(Rooms.class, "standard");
            case SPECIAL:
                return Messages.get(Rooms.class, "special");
            case SECRET:
                return Messages.get(Rooms.class, "secret");
        }
        return null;
    }

    public Image getImage() {
        switch (Rooms.this) {
            case SEWER:
                return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.EMPTY_SP);
            case PRISON:
                return new TileSprite(Assets.Environment.TILES_PRISON, Terrain.EMPTY_SP);
            case CAVES:
                return new TileSprite(Assets.Environment.TILES_CAVES, Terrain.EMPTY_SP);
            case CITY:
                return new TileSprite(Assets.Environment.TILES_CITY, Terrain.EMPTY_SP);
            case HALLS:
                return new TileSprite(Assets.Environment.TILES_HALLS, Terrain.EMPTY_SP);
            case STANDARD:
                return new ItemSprite();
            case SPECIAL:
                return Icons.get(Icons.TALENT);
            case SECRET:
                Image img = new Image(Assets.Interfaces.TOOLBAR);
                img.frame(193, 0, 16, 16);
                return img;
        }
        return new ItemSprite(ItemSpriteSheet.SOMETHING);
    }

    private Class<?>[] classes;

    public Class<?>[] classes() {
        return classes;
    }


    static {

        //TODO Exit/Entrance

        SEWER.classes = new Class[]{
                SewerPipeRoom.class,
                RingRoom.class,
                CircleBasinRoom.class
        };

        PRISON.classes = new Class[]{
                SegmentedRoom.class,
                PillarsRoom.class,
                CellBlockRoom.class,
                RotGardenRoom.class,
                RitualSiteRoom.class,
                MassGraveRoom.class

        };

        CAVES.classes = new Class[]{
                CaveRoom.class,
                CavesFissureRoom.class,
                CirclePitRoom.class,
                BlacksmithRoom.class
        };

        CITY.classes = new Class[]{
                HallwayRoom.class,
                StatuesRoom.class,
                SegmentedLibraryRoom.class
        };

        HALLS.classes = new Class[]{
                RuinsRoom.class,
                ChasmRoom.class,
                SkullsRoom.class,
                DemonSpawnerRoom.class
        };

        SPECIAL.classes = new Class[]{

                ArmoryRoom.class,
                CryptRoom.class,
                GardenRoom.class,
                StorageRoom.class,
                TreasuryRoom.class,
                LaboratoryRoom.class,
                RunestoneRoom.class,
                MagicWellRoom.class,
                SacrificeRoom.class,

                StatueRoom.class,

                PoolRoom.class,
                TrapsRoom.class,
                ToxicGasRoom.class,
                MagicalFireRoom.class,
                SentryRoom.class,


                CrystalChoiceRoom.class,
                CrystalPathRoom.class,
                CrystalVaultRoom.class,

                ShopRoom.class,
//                ImpShopRoom.class,
                RatKingRoom.class,
                WeakFloorRoom.class,
                PitRoom.class


        };

        STANDARD.classes = new Class[]{
                EmptyRoom.class,
                PlantsRoom.class,
                AquariumRoom.class,
                PlatformRoom.class,
                BurnedRoom.class,
                FissureRoom.class,
                GrassyGraveRoom.class,
                StripedRoom.class,
                StudyRoom.class,
                LibraryRoom.class,
                SuspiciousChestRoom.class,
                MinefieldRoom.class
        };

        SECRET.classes = new Class[]{


                SecretArtilleryRoom.class,
                SecretLibraryRoom.class,
                SecretRunestoneRoom.class,
                SecretLaboratoryRoom.class,
                SecretWellRoom.class,
                SecretGardenRoom.class,
                SecretHoneypotRoom.class,
                SecretLarderRoom.class,
                SecretMazeRoom.class,
                SecretChestChasmRoom.class,
                SecretHoardRoom.class,
                SecretSummoningRoom.class

        };

    }

    public static class RoomBag extends EditorItemBag {
        private final Rooms rooms;

        public RoomBag(Rooms rooms) {
            super(null, 0);
            this.rooms = rooms;
            for (Class<?> r : rooms.classes) {
                items.add(new RoomItem((Room) Reflection.newInstance(r)));
            }
        }

        @Override
        public Image getCategoryImage() {
            return rooms.getImage();
        }

        @Override
        public String name() {
            return rooms.getName();
        }
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0) {};

    static {
        for (Rooms r : values()) {
            bag.items.add(new RoomBag(r));
        }
    }
}