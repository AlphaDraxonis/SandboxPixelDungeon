package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.DiamondGooRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.ThickPillarsGooRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.ThinPillarsGooRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.WalledGooRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.CustomRoom;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.WndNewCustomObject;
import com.watabou.noosa.Image;

import java.util.Locale;

public final class Rooms extends GameObjectCategory<Room> {

    private static Rooms instance = new Rooms();

    private final Sewer SEWER = new Sewer();
    private final Prison PRISON = new Prison();
    private final Caves CAVES = new Caves();
    private final City CITY = new City();
    private final Halls HALLS = new Halls();
    private final Standard STANDARD = new Standard();
    private final Special SPECIAL = new Special();
    private final Secret SECRET = new Secret();

    {
        values = new RoomCategory[] {
                SEWER,
                PRISON,
                CAVES,
                CITY,
                HALLS,
                STANDARD,
                SPECIAL,
                SECRET
        };
    }

    private Rooms() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static Rooms instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }

    @Override
    public void updateCustomObjects() {
        updateCustomObjects(Room.class);
    }

    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return new ScrollingListPane.ListButton() {
            protected RedButton createButton() {
                return new RedButton(Messages.get(Rooms.class, "add_custom_obj")) {
                    @Override
                    protected void onClick() {
                        DungeonScene.show(new WndNewCustomObject(CustomRoom.class));
                    }
                };
            }
        };
    }

    private static abstract class RoomCategory extends GameObjectCategory.SubCategory<Room> {

        private RoomCategory(Class<?>[] classes) {
            super(classes);
        }

        @Override
        public Image getSprite() {
            return EditorUtilities.getTerrainFeatureTexture(116);//Ice cap
        }

        @Override
        public String getName() {
            if (getClass().equals(Sewer.class))  return Document.INTROS.pageTitle("Sewers");
            else if (getClass().equals(Prison.class)) return Document.INTROS.pageTitle("Prison");
            else if (getClass().equals(Caves.class))  return Document.INTROS.pageTitle("Caves");
            else if (getClass().equals(City.class))   return Document.INTROS.pageTitle("City");
            else if (getClass().equals(Halls.class))  return Document.INTROS.pageTitle("Halls");
            return Messages.get(Rooms.class, messageKey());
        }

        @Override
        public String messageKey() {
            return getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
        }
    }

    //TODO entrances/exits

    private static final class Sewer extends Rooms.RoomCategory {

        private Sewer() {
            super(new Class[] {
                    SewerPipeRoom.class,
                    RingRoom.class,
                    CircleBasinRoom.class,
                    WaterBridgeRoom.class,

                    DiamondGooRoom.class,
                    WalledGooRoom.class,
                    ThinPillarsGooRoom.class,
                    ThickPillarsGooRoom.class
            });
        }

        @Override
        public Image getSprite() {
            return new TileSprite(Assets.Environment.TILES_SEWERS, Terrain.EMPTY_SP);
        }
    }

    private static final class Prison extends Rooms.RoomCategory {

        private Prison() {
            super(new Class[] {
                    SegmentedRoom.class,
                    PillarsRoom.class,
                    CellBlockRoom.class,
                    ChasmBridgeRoom.class,
                    RotGardenRoom.class,
                    RitualSiteRoom.class,
                    MassGraveRoom.class
            });
        }

        @Override
        public Image getSprite() {
            return new TileSprite(Assets.Environment.TILES_PRISON, Terrain.EMPTY_SP);
        }
    }

    private static final class Caves extends Rooms.RoomCategory {

        private Caves() {
            super(new Class[] {
                    CaveRoom.class,
                    CavesFissureRoom.class,
                    CirclePitRoom.class,
                    CircleWallRoom.class,
                    BlacksmithRoom.class
            });
        }

        @Override
        public Image getSprite() {
            return new TileSprite(Assets.Environment.TILES_CAVES, Terrain.EMPTY_SP);
        }
    }

    private static final class City extends Rooms.RoomCategory {

        private City() {
            super(new Class[] {
                    HallwayRoom.class,
                    StatuesRoom.class,
                    SegmentedLibraryRoom.class,
                    LibraryRingRoom.class
            });
        }

        @Override
        public Image getSprite() {
            return new TileSprite(Assets.Environment.TILES_CITY, Terrain.EMPTY_SP);
        }
    }

    private static final class Halls extends Rooms.RoomCategory {

        private Halls() {
            super(new Class[] {
                    RuinsRoom.class,
                    ChasmRoom.class,
                    SkullsRoom.class,
                    RitualRoom.class,
                    DemonSpawnerRoom.class
            });
        }

        @Override
        public Image getSprite() {
            return new TileSprite(Assets.Environment.TILES_HALLS, Terrain.EMPTY_SP);
        }
    }

    private static final class Standard extends Rooms.RoomCategory {

        private Standard() {
            super(new Class[] {
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
            });
        }

        @Override
        public Image getSprite() {
            return new ItemSprite();
        }
    }

    private static final class Special extends Rooms.RoomCategory {

        private Special() {
            super(new Class[] {
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
            });
        }

        @Override
        public Image getSprite() {
            return Icons.get(Icons.TALENT);
        }
    }

    private static final class Secret extends Rooms.RoomCategory {

        private Secret() {
            super(new Class[] {
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
            });
        }

        @Override
        public Image getSprite() {
            Image img = new Image(Assets.Interfaces.TOOLBAR);
            img.frame(193, 0, 16, 16);
            return img;
        }
    }

}