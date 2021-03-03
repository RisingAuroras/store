package com.example.cloudmusic.migration;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Realm数据库迁移
 */
public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        /**
         * 第一次迁移
         */
        if(oldVersion == 0){
            schema.create("MusicModel")
                    .addField("musicId",String.class)
                    .addField("name",String.class)
                    .addField("poster",String.class)
                    .addField("path",String.class)
                    .addField("author",String.class);

            schema.create("AlbumModel")
                    .addField("albumId",String.class)
                    .addField("name",String.class)
                    .addField("poster",String.class)
                    .addField("playNum",String.class)
                    .addRealmListField("list",schema.get("MusicModel"));

            schema.create("MusicSourceModel")
                    .addRealmListField("album",schema.get("AlbumModel"))
                    .addRealmListField("hot",schema.get("MusicModel"));

            oldVersion = newVersion;
        }
    }
}
