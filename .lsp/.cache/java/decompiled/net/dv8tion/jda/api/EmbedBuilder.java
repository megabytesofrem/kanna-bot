/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.awt.Color
 *  java.lang.CharSequence
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.time.DateTimeException
 *  java.time.Instant
 *  java.time.LocalDateTime
 *  java.time.OffsetDateTime
 *  java.time.ZoneId
 *  java.time.ZoneOffset
 *  java.time.temporal.TemporalAccessor
 *  java.util.Collection
 *  java.util.LinkedList
 *  java.util.List
 *  java.util.regex.Pattern
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.dv8tion.jda.api.entities.EmbedType
 *  net.dv8tion.jda.api.entities.MessageEmbed
 *  net.dv8tion.jda.api.entities.MessageEmbed$AuthorInfo
 *  net.dv8tion.jda.api.entities.MessageEmbed$Field
 *  net.dv8tion.jda.api.entities.MessageEmbed$Footer
 *  net.dv8tion.jda.api.entities.MessageEmbed$ImageInfo
 *  net.dv8tion.jda.api.entities.MessageEmbed$Thumbnail
 *  net.dv8tion.jda.internal.entities.EntityBuilder
 *  net.dv8tion.jda.internal.utils.Checks
 *  net.dv8tion.jda.internal.utils.Helpers
 */
package net.dv8tion.jda.api;

import java.awt.Color;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class EmbedBuilder {
    public static final String ZERO_WIDTH_SPACE = "\u200e";
    public static final Pattern URL_PATTERN = Pattern.compile((String)"\\s*(https?|attachment)://\\S+\\s*", (int)2);
    private final List<MessageEmbed.Field> fields = new LinkedList();
    private final StringBuilder description = new StringBuilder();
    private int color = 0x1FFFFFFF;
    private String url;
    private String title;
    private OffsetDateTime timestamp;
    private MessageEmbed.Thumbnail thumbnail;
    private MessageEmbed.AuthorInfo author;
    private MessageEmbed.Footer footer;
    private MessageEmbed.ImageInfo image;

    public EmbedBuilder() {
    }

    public EmbedBuilder(@Nullable EmbedBuilder builder) {
        this.copyFrom(builder);
    }

    public EmbedBuilder(@Nullable MessageEmbed embed) {
        this.copyFrom(embed);
    }

    @Nonnull
    public MessageEmbed build() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot build an empty embed!");
        }
        if (this.description.length() > 4096) {
            throw new IllegalStateException(Helpers.format((String)"Description is longer than %d! Please limit your input!", (Object[])new Object[]{4096}));
        }
        if (this.length() > 6000) {
            throw new IllegalStateException("Cannot build an embed with more than 6000 characters!");
        }
        String description = this.description.length() < 1 ? null : this.description.toString();
        return EntityBuilder.createMessageEmbed((String)this.url, (String)this.title, (String)description, (EmbedType)EmbedType.RICH, (OffsetDateTime)this.timestamp, (int)this.color, (MessageEmbed.Thumbnail)this.thumbnail, null, (MessageEmbed.AuthorInfo)this.author, null, (MessageEmbed.Footer)this.footer, (MessageEmbed.ImageInfo)this.image, (List)new LinkedList(this.fields));
    }

    @Nonnull
    public EmbedBuilder clear() {
        this.description.setLength(0);
        this.fields.clear();
        this.url = null;
        this.title = null;
        this.timestamp = null;
        this.color = 0x1FFFFFFF;
        this.thumbnail = null;
        this.author = null;
        this.footer = null;
        this.image = null;
        return this;
    }

    public void copyFrom(@Nullable EmbedBuilder builder) {
        if (builder != null) {
            this.setDescription(builder.description.toString());
            this.clearFields();
            this.fields.addAll(builder.fields);
            this.url = builder.url;
            this.title = builder.title;
            this.timestamp = builder.timestamp;
            this.color = builder.color;
            this.thumbnail = builder.thumbnail;
            this.author = builder.author;
            this.footer = builder.footer;
            this.image = builder.image;
        }
    }

    public void copyFrom(@Nullable MessageEmbed embed) {
        if (embed != null) {
            this.setDescription(embed.getDescription());
            this.clearFields();
            this.fields.addAll((Collection)embed.getFields());
            this.url = embed.getUrl();
            this.title = embed.getTitle();
            this.timestamp = embed.getTimestamp();
            this.color = embed.getColorRaw();
            this.thumbnail = embed.getThumbnail();
            this.author = embed.getAuthor();
            this.footer = embed.getFooter();
            this.image = embed.getImage();
        }
    }

    public boolean isEmpty() {
        return (this.title == null || this.title.trim().isEmpty()) && this.timestamp == null && this.thumbnail == null && this.author == null && this.footer == null && this.image == null && this.color == 0x1FFFFFFF && this.description.length() == 0 && this.fields.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int length() {
        int length = this.description.toString().trim().length();
        List<MessageEmbed.Field> list = this.fields;
        synchronized (list) {
            length = (Integer)this.fields.stream().map(f -> f.getName().length() + f.getValue().length()).reduce((Object)length, Integer::sum);
        }
        if (this.title != null) {
            length += this.title.length();
        }
        if (this.author != null) {
            length += this.author.getName().length();
        }
        if (this.footer != null) {
            length += this.footer.getText().length();
        }
        return length;
    }

    public boolean isValidLength() {
        int length = this.length();
        return length <= 6000;
    }

    @Nonnull
    public EmbedBuilder setTitle(@Nullable String title) {
        return this.setTitle(title, null);
    }

    @Nonnull
    public EmbedBuilder setTitle(@Nullable String title, @Nullable String url) {
        if (title == null) {
            this.title = null;
            this.url = null;
        } else {
            Checks.notEmpty((CharSequence)title, (String)"Title");
            Checks.check((title.length() <= 256 ? 1 : 0) != 0, (String)"Title cannot be longer than %d characters.", (Object)256);
            if (Helpers.isBlank((CharSequence)url)) {
                url = null;
            }
            this.urlCheck(url);
            this.title = title;
            this.url = url;
        }
        return this;
    }

    @Nonnull
    public StringBuilder getDescriptionBuilder() {
        return this.description;
    }

    @Nonnull
    public final EmbedBuilder setDescription(@Nullable CharSequence description) {
        this.description.setLength(0);
        if (description != null && description.length() >= 1) {
            this.appendDescription(description);
        }
        return this;
    }

    @Nonnull
    public EmbedBuilder appendDescription(@Nonnull CharSequence description) {
        Checks.notNull((Object)description, (String)"description");
        Checks.check((this.description.length() + description.length() <= 4096 ? 1 : 0) != 0, (String)"Description cannot be longer than %d characters.", (Object)4096);
        this.description.append(description);
        return this;
    }

    @Nonnull
    public EmbedBuilder setTimestamp(@Nullable TemporalAccessor temporal) {
        if (temporal == null) {
            this.timestamp = null;
        } else if (temporal instanceof OffsetDateTime) {
            this.timestamp = (OffsetDateTime)temporal;
        } else {
            ZoneOffset offset;
            try {
                offset = ZoneOffset.from((TemporalAccessor)temporal);
            }
            catch (DateTimeException ignore) {
                offset = ZoneOffset.UTC;
            }
            try {
                LocalDateTime ldt = LocalDateTime.from((TemporalAccessor)temporal);
                this.timestamp = OffsetDateTime.of((LocalDateTime)ldt, (ZoneOffset)offset);
            }
            catch (DateTimeException ignore) {
                try {
                    Instant instant = Instant.from((TemporalAccessor)temporal);
                    this.timestamp = OffsetDateTime.ofInstant((Instant)instant, (ZoneId)offset);
                }
                catch (DateTimeException ex) {
                    throw new DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), (Throwable)ex);
                }
            }
        }
        return this;
    }

    @Nonnull
    public EmbedBuilder setColor(@Nullable Color color) {
        this.color = color == null ? 0x1FFFFFFF : color.getRGB();
        return this;
    }

    @Nonnull
    public EmbedBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    @Nonnull
    public EmbedBuilder setThumbnail(@Nullable String url) {
        if (url == null) {
            this.thumbnail = null;
        } else {
            this.urlCheck(url);
            this.thumbnail = new MessageEmbed.Thumbnail(url, null, 0, 0);
        }
        return this;
    }

    @Nonnull
    public EmbedBuilder setImage(@Nullable String url) {
        if (url == null) {
            this.image = null;
        } else {
            this.urlCheck(url);
            this.image = new MessageEmbed.ImageInfo(url, null, 0, 0);
        }
        return this;
    }

    @Nonnull
    public EmbedBuilder setAuthor(@Nullable String name) {
        return this.setAuthor(name, null, null);
    }

    @Nonnull
    public EmbedBuilder setAuthor(@Nullable String name, @Nullable String url) {
        return this.setAuthor(name, url, null);
    }

    @Nonnull
    public EmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        if (name == null) {
            this.author = null;
        } else {
            Checks.check((name.length() <= 256 ? 1 : 0) != 0, (String)"Name cannot be longer than %d characters.", (Object)256);
            this.urlCheck(url);
            this.urlCheck(iconUrl);
            this.author = new MessageEmbed.AuthorInfo(name, url, iconUrl, null);
        }
        return this;
    }

    @Nonnull
    public EmbedBuilder setFooter(@Nullable String text) {
        return this.setFooter(text, null);
    }

    @Nonnull
    public EmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
        if (text == null) {
            this.footer = null;
        } else {
            Checks.check((text.length() <= 2048 ? 1 : 0) != 0, (String)"Text cannot be longer than %d characters.", (Object)2048);
            this.urlCheck(iconUrl);
            this.footer = new MessageEmbed.Footer(text, iconUrl, null);
        }
        return this;
    }

    @Nonnull
    public EmbedBuilder addField(@Nullable MessageEmbed.Field field) {
        return field == null ? this : this.addField(field.getName(), field.getValue(), field.isInline());
    }

    @Nonnull
    public EmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline) {
        if (name == null && value == null) {
            return this;
        }
        this.fields.add((Object)new MessageEmbed.Field(name, value, inline));
        return this;
    }

    @Nonnull
    public EmbedBuilder addBlankField(boolean inline) {
        this.fields.add((Object)new MessageEmbed.Field(ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, inline));
        return this;
    }

    @Nonnull
    public EmbedBuilder clearFields() {
        this.fields.clear();
        return this;
    }

    @Nonnull
    public List<MessageEmbed.Field> getFields() {
        return this.fields;
    }

    private void urlCheck(@Nullable String url) {
        if (url != null) {
            Checks.check((url.length() <= 2000 ? 1 : 0) != 0, (String)"URL cannot be longer than %d characters.", (Object)2000);
            Checks.check((boolean)URL_PATTERN.matcher((CharSequence)url).matches(), (String)"URL must be a valid http(s) or attachment url.");
        }
    }
}
