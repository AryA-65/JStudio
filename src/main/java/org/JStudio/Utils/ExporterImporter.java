package org.JStudio.Utils;

import org.JStudio.Core.Song;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExporterImporter { //move export audio and file loader to this class
    public static void exportSong(Song song, String f_name) throws IOException {
        Path musicDir = Paths.get(System.getProperty("user.home"), "Music", "JStudio", "saved_songs");
        if (!musicDir.toFile().exists()) Files.createDirectories(musicDir);

        File song_f = musicDir.resolve(f_name + ".song").toFile();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(song_f))) {
                out.writeObject(song);
        }
    }

    public static Song importSong(String f_name) throws IOException, ClassNotFoundException {
        Path song_f = Paths.get(System.getProperty("user.home"), "Music", "JStudio", "saved_songs", f_name + ".song");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(song_f.toFile()))) {
            return (Song) in.readObject();
        }
    }

    //testing (hierachical format)
//    public void exportSongHierarchical(Song song, Path baseDir) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        Files.createDirectories(baseDir);
//
//        // Save song meta
//        mapper.writeValue(baseDir.resolve("song.meta").toFile(), song);
//
//        // Save shared clips
//        Path clipDir = baseDir.resolve("clips");
//        Files.createDirectories(clipDir);
//
//        Set<String> exportedClips = new HashSet<>();
//
//        for (Track track : song.tracks) {
//            for (Clip clip : track.clips) {
//                if (clip.clipFilePath != null && exportedClips.add(clip.clipFilePath)) {
//                    Path clipFile = clipDir.resolve(Paths.get(clip.clipFilePath).getFileName());
//                    mapper.writeValue(clipFile.toFile(), clip);
//                }
//            }
//        }
//
//        // Save tracks
//        Path tracksDir = baseDir.resolve("tracks");
//        Files.createDirectories(tracksDir);
//
//        for (int i = 0; i < song.tracks.size(); i++) {
//            mapper.writeValue(tracksDir.resolve(i + ".track").toFile(), song.tracks.get(i));
//        }
//    }
//
//
//    public Song importSongHierarchical(Path baseDir) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//
//        // Load song
//        Song song = mapper.readValue(baseDir.resolve("song.meta").toFile(), Song.class);
//
//        // Load clips
//        Path clipDir = baseDir.resolve("clips");
//        Map<String, Clip> clipCache = new HashMap<>();
//
//        // Load tracks and resolve clips
//        Path tracksDir = baseDir.resolve("tracks");
//        for (int i = 0; i < song.tracks.size(); i++) {
//            Track track = mapper.readValue(tracksDir.resolve(i + ".track").toFile(), Track.class);
//
//            for (int j = 0; j < track.clips.size(); j++) {
//                Clip c = track.clips.get(j);
//                if (c.clipFilePath != null) {
//                    Path clipFile = clipDir.resolve(Paths.get(c.clipFilePath).getFileName());
//                    if (Files.exists(clipFile)) {
//                        Clip loaded = mapper.readValue(clipFile.toFile(), Clip.class);
//                        track.clips.set(j, loaded);
//                    } else {
//                        // Leave reference as-is if file missing
//                    }
//                }
//            }
//
//            song.tracks.set(i, track);
//        }
//
//        return song;
//    }

}
