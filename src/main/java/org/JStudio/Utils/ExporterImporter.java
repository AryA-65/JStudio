package org.JStudio.Utils;

import org.JStudio.Core.Song;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class that export and imports a song
 */
public class ExporterImporter { //move export audio and file loader to this class
    /**
     * Exports a Song object to the user's local file system.
     * The file will be saved in the directory: {/Music/JStudio/saved_songs/}
     * with a ".song" extension.
     * If the directory doesn't exist, it will be created.
     *
     * @param song   The object to export. Must implement
     * @param f_name The file name (without an extension) under which the song will be saved
     * @throws IOException If an I/O error occurs during writing the file
     */
    public static void exportSong(Song song, String f_name) throws IOException {
        Path musicDir = Paths.get(System.getProperty("user.home"), "Music", "JStudio", "saved_songs");
        if (!musicDir.toFile().exists()) Files.createDirectories(musicDir);

        File song_f = musicDir.resolve(f_name + ".song").toFile();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(song_f))) {
                out.writeObject(song);
        }
    }

    /**
     * Imports a Song object from a file stored on the local file system.
     * The file is expected to be located in: {/Music/JStudio/saved_songs/}
     * and have a ".song" extension.
     *
     * @param f_name The name of the file (without extension) to import the song from.
     * @return The deserialized Song object.
     * @throws IOException            If an I/O error occurs during file access
     * @throws ClassNotFoundException If the class definition of the serialized object is not found
     */
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
