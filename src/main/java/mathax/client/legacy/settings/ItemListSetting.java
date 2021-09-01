package mathax.client.legacy.settings;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemListSetting extends Setting<List<Item>> {
    public final Predicate<Item> filter;
    private boolean bypassFilterWhenSavingAndLoading;

    public ItemListSetting(String name, String description, List<Item> defaultValue, Consumer<List<Item>> onChanged, Consumer<Setting<List<Item>>> onModuleActivated, IVisible visible, Predicate<Item> filter, boolean bypassFilterWhenSavingAndLoading) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.value = new ArrayList<>(defaultValue);
        this.filter = filter;
        this.bypassFilterWhenSavingAndLoading = bypassFilterWhenSavingAndLoading;
    }

    @Override
    protected List<Item> parseImpl(String str) {
        String[] values = str.split(",");
        List<Item> items = new ArrayList<>(values.length);

        try {
            for (String value : values) {
                Item item = parseId(Registry.ITEM, value);
                if (item != null && (filter == null || filter.test(item))) items.add(item);
            }
        } catch (Exception ignored) {}

        return items;
    }

    @Override
    public void reset(boolean callbacks) {
        value = new ArrayList<>(defaultValue);
        if (callbacks) changed();
    }

    @Override
    protected boolean isValueValid(List<Item> value) {
        return true;
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return Registry.ITEM.getIds();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = saveGeneral();

        NbtList valueTag = new NbtList();
        for (Item item : get()) {
            if (bypassFilterWhenSavingAndLoading || (filter == null || filter.test(item))) valueTag.add(NbtString.of(Registry.ITEM.getId(item).toString()));
        }
        tag.put("value", valueTag);

        return tag;
    }

    @Override
    public List<Item> fromTag(NbtCompound tag) {
        get().clear();

        NbtList valueTag = tag.getList("value", 8);
        for (NbtElement tagI : valueTag) {
            Item item = Registry.ITEM.get(new Identifier(tagI.asString()));

            if (bypassFilterWhenSavingAndLoading || (filter == null || filter.test(item))) get().add(item);
        }

        changed();
        return get();
    }

    public static class Builder {
        private String name = "undefined", description = "";
        private List<Item> defaultValue;
        private Consumer<List<Item>> onChanged;
        private Consumer<Setting<List<Item>>> onModuleActivated;
        private IVisible visible;
        private Predicate<Item> filter;
        private boolean bypassFilterWhenSavingAndLoading;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder defaultValue(List<Item> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder onChanged(Consumer<List<Item>> onChanged) {
            this.onChanged = onChanged;
            return this;
        }

        public Builder onModuleActivated(Consumer<Setting<List<Item>>> onModuleActivated) {
            this.onModuleActivated = onModuleActivated;
            return this;
        }

        public Builder visible(IVisible visible) {
            this.visible = visible;
            return this;
        }

        public Builder filter(Predicate<Item> filter) {
            this.filter = filter;
            return this;
        }

        public Builder bypassFilterWhenSavingAndLoading() {
            this.bypassFilterWhenSavingAndLoading = true;
            return this;
        }

        public ItemListSetting build() {
            return new ItemListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter, bypassFilterWhenSavingAndLoading);
        }
    }
}
