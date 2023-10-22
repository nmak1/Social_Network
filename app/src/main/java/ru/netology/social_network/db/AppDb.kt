package ru.netology.social_network.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.social_network.dao.PostDao
import ru.netology.social_network.dao.PostRemoteKeyDao
import ru.netology.social_network.entity.PostEntity
import ru.netology.social_network.entity.PostRemoteKeyEntity



@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class],
    version = 2,
    exportSchema = false)

abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}