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

    public static final Creator<CourseModel> CREATOR = new Creator<CourseModel>() {
        public CourseModel createFromParcel(Parcel in) { return new CourseModel(in); }
        public CourseModel[] newArray(int size) { return new CourseModel[size]; }
    };


    public static class Slide implements Parcelable {
        public String imageUrl;
        public String text;
        public String backgroundColor;
        public String textColor;
        public Answer[] answers;
        public Button[] buttons;

        public Slide(Parcel in) {
            imageUrl = in.readString();
            text = in.readString();
            backgroundColor = in.readString();
            textColor = in.readString();
            answers = in.createTypedArray(Answer.CREATOR);
            buttons = in.createTypedArray(Button.CREATOR);
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
            out.writeString(textColor);
            out.writeTypedArray(answers, 0);
            out.writeTypedArray(buttons, 0);
        }

        public static final Creator<Slide> CREATOR = new Creator<Slide>() {
            public Slide createFromParcel(Parcel in) { return new Slide(in); }
            public Slide[] newArray(int size) { return new Slide[size]; }
        };

        public static class Answer implements Parcelable {
            public String text;
            public String hint;
            public boolean correct;

            public Answer(Parcel in) {
                text = in.readString();
                hint = in.readString();
                correct = in.readInt() != 0;
            }

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel out, int i) {
                out.writeString(text);
                out.writeString(hint);
                out.writeInt(correct ? 1 : 0);
            }

            public static final Creator<Answer> CREATOR = new Creator<Answer>() {
                public Answer createFromParcel(Parcel in) { return new Answer(in); }
                public Answer[] newArray(int size) { return new Answer[size]; }
            };
        }

        public static class Button implements Parcelable {
            public String text;
            public String action;

            public Button(Parcel in) {
                text = in.readString();
                action = in.readString();
            }

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(Parcel out, int i) {
                out.writeString(text);
                out.writeString(action);
            }

            public static final Creator<Button> CREATOR = new Creator<Button>() {
                public Button createFromParcel(Parcel in) { return new Button(in); }
                public Button[] newArray(int size) { return new Button[size]; }
            };
        }
    }
}
