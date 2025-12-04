package frontend;

import backend.Media;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Loads movie posters from local resources. Titles are normalized to match the filenames
 * in resources/MoviePosters.
 */
public final class PosterLoader {
    private static final String POSTER_ROOT = "/resources/MoviePosters/";
    private static final String[] EXTENSIONS = {"jpg", "jpeg", "png"};

    private PosterLoader() {
    }

    public static ImageIcon load(Media media, int width, int height) {
        if (media == null) {
            return null;
        }
        return loadLocal(media.getTitle(), width, height);
    }

    private static ImageIcon loadLocal(String title, int width, int height) {
        if (title == null || title.isBlank()) {
            return null;
        }

        for (String guess : buildFilenameCandidates(title)) {
            for (String ext : EXTENSIONS) {
                ImageIcon icon = readImage(POSTER_ROOT + guess + "." + ext, width, height);
                if (icon != null) {
                    return icon;
                }
            }
        }

        return null;
    }

    private static ImageIcon readImage(String resourcePath, int width, int height) {
        try {
            URL resource = PosterLoader.class.getResource(resourcePath);
            if (resource != null) {
                Image image = ImageIO.read(resource);
                if (image != null) {
                    return new ImageIcon(scaleImage(image, width, height));
                }
            }
        } catch (Exception e) {
            // ignore and let caller try other options
        }
        return null;
    }

    private static String normalize(String value) {
        String noAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return noAccents.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    private static List<String> buildFilenameCandidates(String rawTitle) {
        Set<String> candidates = new LinkedHashSet<>();
        String trimmed = rawTitle.trim();

        // Direct title, as entered
        candidates.add(trimmed);

        // Remove all spaces
        candidates.add(trimmed.replaceAll("\\s+", ""));

        // Drop punctuation but keep hyphens
        candidates.add(trimmed.replaceAll("[:'’.,!?]", "").replaceAll("\\s+", ""));

        // Replace colons with hyphens
        candidates.add(trimmed.replace(":", "-").replaceAll("\\s+", ""));

        // Fully normalized (strip accents and punctuation, lowercase)
        candidates.add(normalize(trimmed));

        return new ArrayList<>(candidates);
    }

    private static Image scaleImage(Image img, int width, int height) {
        return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}
