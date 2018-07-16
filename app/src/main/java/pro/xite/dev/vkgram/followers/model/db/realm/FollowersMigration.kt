package pro.xite.dev.vkgram.followers.model.db.realm

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import timber.log.Timber

/**
 * Created by Roman Syrchin on 7/16/18.
 */

class FollowersMigration : RealmMigration {

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {

        // https://github.com/realm/realm-java/blob/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample/model/Migration.java
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        val schema = realm?.schema
        var growingVersion = oldVersion

        Timber.v("Migration check and apply %d -> %d", oldVersion, newVersion)

        /************************************************
        // Version 0
        nothing

        // Version 1
        class RealmVkUser
        @PrimaryKey
        realm_id: Int

        @io.realm.annotations.Required
        @io.realm.annotations.Index
        vk_id: Long

        first_name: String
        last_name: String
         ************************************************/
        if (growingVersion == 0L) {
            schema?.create ("RealmVkUser")!!
                    .addField("realm_id",   Int::class.java,  FieldAttribute.PRIMARY_KEY)
                    .addField("vk_id",      Long::class.java, FieldAttribute.INDEXED)
                    .addField("first_name", String::class.java)
                    .addField("last_name",  String::class.java)
            growingVersion++
            Timber.v("Migrated to %d", growingVersion)
        }

        /************************************************
        // Version 2
         ************************************************/
        if (growingVersion == 1L) {
            // TODO U need to implement migration 1 -> 2
            Timber.v("Migrated to %d", growingVersion)
        }
    }

}