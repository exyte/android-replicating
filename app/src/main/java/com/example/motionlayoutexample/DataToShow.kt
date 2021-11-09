package com.example.motionlayoutexample

import com.example.motionlayoutexample.entities.AlbumEntity
import com.example.motionlayoutexample.entities.CommentEntity
import com.example.motionlayoutexample.entities.TrackEntity

val popularComments = listOf(
        CommentEntity("1", "Ludia Lopez", "AllInSoftInside", "Feb \n 17", R.drawable.human_photo1),
        CommentEntity("1", "Aurora Sink", "Love it", "Jule\n 17 ", R.drawable.human_photo2),
        CommentEntity("1", "Bryan King", "This is awesome", "Feb \n 5", R.drawable.human_photo3),
        CommentEntity("1", "Lilia Tailer", "lovely album", "Jan \n 3", R.drawable.human_photo4),
        CommentEntity("1", "Alex Ring", "Great", "May \n 21", R.drawable.human_photo5),
)

val comments = listOf(
        CommentEntity("1", "Ludia Lopez", "AllInSoftInside", "Feb \n 17", R.drawable.human_photo6),
        CommentEntity("1", "Alex Ring", "AllInSoftInside", "Feb\n 22", R.drawable.human_photo7),
        CommentEntity("1", "Aurora", "AllInSoftInside", "Feb\n2", R.drawable.human_photo3),
        CommentEntity("1", "Bryan King", "AllInSoftInside", "Feb\n3", R.drawable.human_photo1),
        CommentEntity("1", "Aurora Sink", "AllInSoftInside", "Feb\n4", R.drawable.human_photo5),
)

val albumList = listOf(
        AlbumEntity("1", R.drawable.album2, "It Happend Quiet", "2018"),
        AlbumEntity("2", R.drawable.album1, "All My Demons", "2016"),
        AlbumEntity("3", R.drawable.album3, "Running With", "2015"),
        AlbumEntity("4", R.drawable.singer1, "For The Humans", "2021"),
        AlbumEntity("5", R.drawable.album4, "Stories", "2021"),
        AlbumEntity("6", R.drawable.album3, "Music For The Free", "2021"))

val tracks = listOf(
        TrackEntity("1.", "Aurora", "All Is Soft Inside", "3:54", false),
        TrackEntity("2.", "Aurora", "Queendom", "2:54", false),
        TrackEntity("3.", "Aurora", "Gentle Earthquakes", "3:54", true),
        TrackEntity("4.", "Aurora", "Awakeing", "3:53", false),
        TrackEntity("5.", "Aurora", "Music For Everything", "4:52", true),
        TrackEntity("6.", "Aurora", "Music For Everything", "4:52", true),
        TrackEntity("7.", "Aurora", "All Is Soft Inside", "3:54", false),
        TrackEntity("8.", "Aurora", "All Is Soft Inside", "3:54", true),
)

const val albumQuantity = "12"
const val followersNumber = "3847"
const val followingNumber = "43"
const val commentNumber = "15"
const val musicianName = "Aurora Aksnes"
const val musicianInfo = "Norwegian singer/songwriter AURORA works in a similar dark pop millieu as artist like Oh Land, Lykke Li."
const val singer = "Aurora"