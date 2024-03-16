package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.RoomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
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
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretMazeRoom;
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
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PoolRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RunestoneRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SacrificeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StatueRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StorageRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ToxicGasRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.TrapsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.TreasuryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.AquariumRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BurnedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CavesFissureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CellBlockRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ChasmRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CircleBasinRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CirclePitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.FissureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.GrassyGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.HallwayRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.MinefieldRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PillarsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PlantsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PlatformRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RingRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RuinsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SegmentedLibraryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SegmentedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SewerPipeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SkullsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StatuesRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StripedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StudyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SuspiciousChestRoom;
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

    public static final EditorItemBag bag = new EditorItemBag("name", 0) {
        @Override
        public Item findItem(Object src) {
            for (Item bag : items) {
                for (Item i : ((Bag) bag).items) {
                    if (((RoomItem) i).room().getClass() == src) return i;
                }
            }
            return null;
        }
    };

    static {
        for (Rooms r : values()) {
            bag.items.add(new RoomBag(r));
        }
    }
}