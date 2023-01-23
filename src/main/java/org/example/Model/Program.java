package org.example.Model;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Class representing a radio program.
 *
 * @author Olivia Harlin
 * @umu-id olha0073
 * @çs-id id19ohn
 *
 * @version 1.0
 * @date 23-01-09
 */
public class Program {
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private URL image;

    /**
     * Class constructor. Convert attributes to correct formats.
     * @param title - The title of the program as a string
     * @param description - The description of the program as a string
     * @param startTimeString - The start time of the program as a string
     * @param endTimeString - The end time of the program as a string
     * @param image - the URL for the cover image as a string
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public Program(String title, String description, String startTimeString, String endTimeString, String image) throws URISyntaxException, MalformedURLException {
        this.title = title;
        this.description = description;
        this.image = new URL(image);

        // convert time strings to localDateTime format
        ZonedDateTime temp1 = Instant.parse(startTimeString).atZone(ZoneId.systemDefault());
        startTime = temp1.toLocalDateTime();
        ZonedDateTime temp2 = Instant.parse(endTimeString).atZone(ZoneId.systemDefault());
        endTime = temp2.toLocalDateTime();
    }

    /**
     * Gets the program title.
     * @return the title as a string
     */
    public String getTitle(){
        return title;
    }

    /**
     * Gets the program description.
     * @return the description as a string
     */
    public String getDescription(){
        return description;
    }

    /**
     * Gets the programs start time
     * @return the start time as LocalDateTime
     */
    public LocalDateTime getStartTime(){
        return startTime;
    }

    /**
     * Gets the programs end time
     * @return the end time as LocalDateTime
     */
    public LocalDateTime getEndTime(){
        return endTime;
    }

    /**
     * Gets the programs cover image
     * @return the image URL
     */
    public URL getImage(){
        return image;
    }


}
