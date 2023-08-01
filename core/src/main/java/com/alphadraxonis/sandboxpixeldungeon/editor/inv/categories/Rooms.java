package com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.RoomItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.journal.Document;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.Room;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.RatKingRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretArtilleryRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretChestChasmRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretGardenRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretHoardRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretHoneypotRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretLaboratoryRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretLarderRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretLibraryRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretMazeRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretRunestoneRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretSummoningRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretWellRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.ArmoryRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.CryptRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.CrystalChoiceRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.CrystalPathRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.CrystalVaultRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.DemonSpawnerRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.GardenRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.LaboratoryRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.MagicWellRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.MassGraveRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.PitRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.PoolRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.RotGardenRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.RunestoneRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.SacrificeRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.SentryRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.ShopRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.StatueRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.StorageRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.ToxicGasRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.TrapsRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.TreasuryRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.AquariumRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.BlacksmithRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.BurnedRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.CaveRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.CavesFissureRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.CellBlockRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.ChasmRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.CircleBasinRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.CirclePitRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.FissureRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.GrassyGraveRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.HallwayRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.ImpShopRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.MinefieldRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.PillarsRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.PlantsRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.PlatformRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.RingRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.RuinsRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.SegmentedLibraryRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.SegmentedRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.SewerPipeRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.SkullsRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.StatuesRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.StripedRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.StudyRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.SuspiciousChestRoom;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public enum Rooms {

    SEWER,
    PRISON,
    CAVES,
    CITY,
    HALLS,
    STANDART,
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
            case STANDART:
                return Messages.get(Rooms.class, "standart");
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
                return new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(Terrain.EMPTY_SP, -1));
            case PRISON:
                return new ItemSprite(Assets.Environment.TILES_PRISON, new TileItem(Terrain.EMPTY_SP, -1));
            case CAVES:
                return new ItemSprite(Assets.Environment.TILES_CAVES, new TileItem(Terrain.EMPTY_SP, -1));
            case CITY:
                return new ItemSprite(Assets.Environment.TILES_CITY, new TileItem(Terrain.EMPTY_SP, -1));
            case HALLS:
                return new ItemSprite(Assets.Environment.TILES_HALLS, new TileItem(Terrain.EMPTY_SP, -1));
            case STANDART:
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
//                RitualSiteRoom.class,
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
                ImpShopRoom.class,
                RatKingRoom.class,
                WeakFloorRoom.class,
                PitRoom.class


        };

        STANDART.classes = new Class[]{
                EmptyRoom.class,
                PlantsRoom.class,
                AquariumRoom.class,
                PlatformRoom.class,
                BurnedRoom.class,
                FissureRoom.class,
                GrassyGraveRoom.class,
                StripedRoom.class,
                StudyRoom.class,
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
            super(rooms.getName(), 0);
            this.rooms = rooms;
            for (Class<?> r : rooms.classes) {
                items.add(new RoomItem((Room) Reflection.newInstance(r)));
            }
        }

        @Override
        public Image getCategoryImage() {
            return rooms.getImage();
        }
    }

    public static final EditorItemBag bag = new EditorItemBag(Messages.get(EditorItemBag.class, "rooms"), 0) {
    };

    static {
        for (Rooms r : values()) {
            bag.items.add(new RoomBag(r));
        }
    }
}