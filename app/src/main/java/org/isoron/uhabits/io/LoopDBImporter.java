/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.io;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.activeandroid.ActiveAndroid;

import org.isoron.uhabits.BuildConfig;
import org.isoron.uhabits.HabitsApplication;
import org.isoron.uhabits.helpers.DatabaseHelper;

import java.io.File;
import java.io.IOException;

public class LoopDBImporter extends AbstractImporter
{
    @Override
    public boolean canHandle(@NonNull File file) throws IOException
    {
        if(!isSQLite3File(file)) return false;

        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null,
                SQLiteDatabase.OPEN_READONLY);

        Cursor c = db.rawQuery("select count(*) from SQLITE_MASTER where name=? or name=?",
                new String[]{"Checkmarks", "Repetitions"});

        boolean result = (c.moveToFirst() && c.getInt(0) == 2);

        c.close();
        return result;
    }

    @Override
    public void importHabitsFromFile(@NonNull File file) throws IOException
    {
        ActiveAndroid.dispose();
        Context context = HabitsApplication.getContext();

        File originalDB = DatabaseHelper.getDatabaseFile(context, BuildConfig.databaseFilename);
        File backupDir = DatabaseHelper.getFilesDir(context, "Backups");

        DatabaseHelper.saveDatabaseCopy(context, backupDir);
        DatabaseHelper.copy(file, originalDB);
        DatabaseHelper.initializeActiveAndroid(context, BuildConfig.databaseFilename);
    }
}
