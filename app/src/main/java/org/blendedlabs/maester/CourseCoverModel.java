package org.blendedlabs.maester;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CourseCoverModel implements Parcelable {
    public String name;
    public String author;
    public String imageUrl;
    public String courseUrl;

    // == equals / hashCode ======================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseCoverModel that = (CourseCoverModel) o;

        if (!name.equals(that.name)) return false;
        if (!author.equals(that.author)) return false;
        if (!imageUrl.equals(that.imageUrl)) return false;
        if (!courseUrl.equals(that.courseUrl)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + author.hashCode();
        result = 31 * result + imageUrl.hashCode();
        result = 31 * result + courseUrl.hashCode();
        return result;
    }

    public CourseCoverModel(Parcel in) {
        name = in.readString();
        author = in.readString();
        imageUrl = in.readString();
        courseUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(name);
        out.writeString(author);
        out.writeString(imageUrl);
        out.writeString(courseUrl);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CourseCoverModel createFromParcel(Parcel in) {
            return new CourseCoverModel(in);
        }

        public CourseCoverModel[] newArray(int size) {
            return new CourseCoverModel[size];
        }
    };


    @SuppressWarnings("serial")
    public static class List extends ArrayList<CourseCoverModel> {}

}
