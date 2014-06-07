package org.blendedlabs.maester;

import android.os.Parcel;
import android.os.Parcelable;

public class CourseModel implements Parcelable {
    public String name;
    public Slide[] slides;

    public CourseModel(Parcel in) {
        name = in.readString();
        slides = in.createTypedArray(Slide.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(name);
        out.writeTypedArray(slides, 0);
    }

    public static final Parcelable.Creator<CourseModel> CREATOR = new Parcelable.Creator<CourseModel>() {
        public CourseModel createFromParcel(Parcel in) {
            return new CourseModel(in);
        }

        public CourseModel[] newArray(int size) {
            return new CourseModel[size];
        }
    };


    public static class Slide implements Parcelable {
        public String imageUrl;
        public String text;
        public String backgroundColor;

        public Slide(Parcel in) {
            imageUrl = in.readString();
            text = in.readString();
            backgroundColor = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int i) {
            out.writeString(imageUrl);
            out.writeString(text);
            out.writeString(backgroundColor);
        }

        public static final Parcelable.Creator<Slide> CREATOR = new Parcelable.Creator<Slide>() {
            public Slide createFromParcel(Parcel in) {
                return new Slide(in);
            }

            public Slide[] newArray(int size) {
                return new Slide[size];
            }
        };
    }
}
