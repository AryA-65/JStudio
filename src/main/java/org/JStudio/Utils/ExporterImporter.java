package org.JStudio.Utils;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.JStudio.Core.Mixer;
import org.JStudio.Core.Song;
import org.JStudio.Core.Track;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
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
    public static boolean saveSong(Song song, String f_name) throws IOException {
        Path musicDir = Paths.get(System.getProperty("user.home"), "Music", "JStudio", "saved_songs");

        if (!Checksum.folderExists(musicDir.toString())) {Checksum.createFolder(musicDir.toString());}

        File song_f = musicDir.resolve(f_name + ".song").toFile();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(song_f))) {
                out.writeObject(song);
                return true;
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
    public static Song loadSong(String f_name) throws IOException, ClassNotFoundException {
        Path song_f = Paths.get(System.getProperty("user.home"), "Music", "JStudio", "saved_songs", f_name + ".song");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(song_f.toFile()))) {
            return (Song) in.readObject();
        }
    }

    /**
     * Loads a Song object by prompting the user to select a file through a file chooser dialog
     * The selected file must be located in the user's "Music/JStudio/saved_songs" directory
     *
     * @return The loaded Song object
     * @throws IOException If there is an error reading the file
     * @throws ClassNotFoundException If the class of the serialized object cannot be found
     */
    public static Song loadSong() throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Song");

        File dir = Paths.get(System.getProperty("user.home"), "Music", "JStudio", "saved_songs").toFile();

        if (!Checksum.folderExists(dir.getAbsolutePath())) Checksum.createFolder(dir.getAbsolutePath());

        fileChooser.setInitialDirectory(dir);

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return null;

        return loadSong(file.getName().substring(0, file.getName().length() - 5));
    }

    /**
     * Exports the given Song as a stereo WAV file using the specified mixer settings.
     * The file is written to the user's "Music/JStudio/exported_Audio" directory.
     *
     * @param song The Song to export.
     * @param f_name The desired filename (without extension)
     * @param mixer The Mixer containing gain and panning informatio
     * @return true if the export is successful.
     * @throws IOException If an error occurs while writing the WAV file.
     */
    public static boolean exportSong(Song song, String f_name, Mixer mixer) throws IOException {
        final int sample_rate = 44100;
        final short buff_size = 1024;
        final byte channels = 2;

        Path exportDir = Paths.get(System.getProperty("user.home"), "Music", "JStudio", "exported_Audio");

        float[][] floatBuffer = new float[2][buff_size];
        byte[] byteBuffer = new byte[buff_size * 4];

        AudioFormat af = new AudioFormat(sample_rate, 16, channels, true, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int total_samples = (int) ((Song.bpm.get() / (Song.bpm.get()) / 60) * sample_rate);
        int processed_samples = 0;

        while (processed_samples < total_samples) {
            for (Track track : song.getTracks()) {
                track.process(floatBuffer, processed_samples, buff_size, sample_rate);
            }

            for (int i = 0; i < buff_size; i++) {
                float left = floatBuffer[0][i] * mixer.getMasterGain().get() * (1 - mixer.getPan().get());
                float right = floatBuffer[1][i] * mixer.getMasterGain().get() * (1 - mixer.getPan().get());

                int sampleL = (int) (left * 32767.0);
                int sampleR = (int) (right * 32767.0);

                sampleL = Math.max(-32768, Math.min(32767, sampleL));
                sampleR = Math.max(-32768, Math.min(32767, sampleR));

                int index = i * 4;
                byteBuffer[index] = (byte) (sampleL & 0xFF);
                byteBuffer[index + 1] = (byte) ((sampleL >> 8) & 0xFF);
                byteBuffer[index + 2] = (byte) (sampleR & 0xFF);
                byteBuffer[index + 3] = (byte) ((sampleR >> 8) & 0xFF);
            }

            processed_samples++;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        AudioInputStream ais = new AudioInputStream(bais, af, byteBuffer.length / af.getFrameSize());

        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, exportDir.resolve(f_name + ".wav").toFile());
        return true;
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
