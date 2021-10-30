package mathax.legacy.client.gui.widgets.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.screens.music.MusicScreen;
import mathax.legacy.client.gui.widgets.containers.WTable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WPlaylistPage extends WMusicWidget {
    private final WPaginationProvider pagination;
    private final Supplier<List<AudioTrack>> source;
    private final Consumer<Integer> select;
    private final Consumer<Integer> remove;

    @Override
    public void add(WTable parent, MusicScreen screen, GuiTheme theme) {
        List<AudioTrack> tracks = source.get();
        pagination.setMaxPage(tracks.size() / MusicScreen.pageSize);
        for (int i = 0; i < tracks.size() - pagination.getPageOffset() && i < MusicScreen.pageSize; i++) {
            int j = i + pagination.getPageOffset();
            if (i != 0) parent.row();
            AudioTrack track = tracks.get(j);
            if (select == null) parent.add(theme.label(screen.getName(track))).expandX();
            else parent.add(theme.button(screen.getName(track))).expandX().widget().action = () -> select.accept(j);

            if (remove != null) parent.add(theme.minus()).right().widget().action = () -> remove.accept(j);
        }

        super.add(parent, screen, theme);
    }

    public WPlaylistPage(WPaginationProvider pagination, Supplier<List<AudioTrack>> source, Consumer<Integer> select, Consumer<Integer> remove) {
        this.pagination = pagination;
        this.source = source;
        this.select = select;
        this.remove = remove;
    }
}
