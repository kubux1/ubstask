package com.example.filecontent.filecontent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileContent {
    @Id
    private String id;
    private String name;
    private String description;
    private Instant updatedTimestamp;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        FileContent otherFile = (FileContent) obj;
        return id.equals(otherFile.id)
                && name.equals(otherFile.name)
                && description.equals(otherFile.description)
                && updatedTimestamp.equals(otherFile.updatedTimestamp);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((updatedTimestamp == null) ? 0 : updatedTimestamp.hashCode());
        return result;
    }
}
