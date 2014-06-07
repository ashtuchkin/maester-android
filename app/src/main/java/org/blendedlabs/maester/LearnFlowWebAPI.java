package org.blendedlabs.maester;

import retrofit.http.GET;

public interface LearnFlowWebAPI {

    @GET("/courses.json")
    CourseCoverModel.List courses();
}
