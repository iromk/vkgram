package pro.xite.dev.vkgram.followers.model.db.realm

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

/**
 * Created by Roman Syrchin on 7/16/18.
 */
open class RealmVkUser : RealmObject() {

    @PrimaryKey
    private var realm_id: Int = 0

    @Index
    private var vk_id: Long = 0L

    private lateinit var first_name: String
    private lateinit var last_name: String



}
