/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2024 Evan Debenham
 *  *
 *  * Sandbox Pixel Dungeon
 *  * Copyright (C) 2023-2024 AlphaDraxonis
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.services.server;

import com.watabou.NotAllowedInLua;

@NotAllowedInLua
public final class ServerConstants {

    private ServerConstants() {}
    
    static final String ACTION_UPLOAD_START = "upload_start";
    static final String ACTION_UPLOAD_FINISH = "upload_finish";
    static final String ACTION_UPDATE_START = "update_start";
    static final String ACTION_UPDATE_FINISH = "update_finish";
    static final String ACTION_UPLOAD_FILE = "upload_file";

    static final String ACTION_BUG_REPORT = "bug_report";

    static final String ACTION_CANCEL = "cancel";
    
    static final String ACTION_GET_PREVIEW_LIST = "get_preview_list";
    public static final String ACTION_GET_LATEST_UPLOAD_TIME = "get_latest_upload_time";
    
    static final String ACTION_START_DOWNLOAD = "start_download";
    public static final String ACTION_DOWNLOAD_FILE = "download_file";
    public static final String ACTION_READ_FILE = "read_file";
    
    static final String ACTION_IS_CREATOR = "is_creator";
    static final String ACTION_DELETE_DUNGEON = "delete_dungeon";
    static final String ACTION_DELETE_VERSION = "delete_version";
    
    
    public static final String KEYWORD_SUCCESS = "success";
    public static final String KEYWORD_BANNED = "banned";
    public static final String KEYWORD_INVALID_DUNGEON_ID = "invalid_dungeon_id";
    
}
