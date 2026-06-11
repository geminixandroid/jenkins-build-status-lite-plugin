package ie.dispensense.buildstatuslite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.BallColor;
import hudson.model.Job;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.mockito.Mockito;

@WithJenkins
class IconRequestHandlerTest {

    @SuppressWarnings("unused")
    private static JenkinsRule jenkinsRule;

    private final IconRequestHandler iconRequestHandler = new IconRequestHandler();

    @BeforeAll
    static void beforeAll(JenkinsRule rule) {
        jenkinsRule = rule;
    }

    @Test
    void handleIconRequest() throws Exception {
        ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
        StatusImage mockedStatusImage = new StatusImage();
        when(mockIconResolver.getImage(BallColor.BLUE, "style", "subject", "status", null, null, null))
                .thenReturn(mockedStatusImage);

        Field field = IconRequestHandler.class.getDeclaredField("iconResolver");
        field.setAccessible(true);
        field.set(iconRequestHandler, mockIconResolver);

        StatusImage statusImage = iconRequestHandler.handleIconRequest("style", "subject", "status");
        assertEquals(mockedStatusImage, statusImage);
    }

    @Test
    void handleIconRequestForJob() throws NoSuchFieldException, IllegalAccessException {
        ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
        StatusImage mockedStatusImage = new StatusImage();
        ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
        Job job = mock(Job.class);

        when(job.getIconColor()).thenReturn(BallColor.BLUE);
        when(mockIconResolver.getImage(BallColor.BLUE, "style", "subject", "status", null, null, null))
                .thenReturn(mockedStatusImage);
        when(mockParameterResolver.resolve(job, "subject")).thenReturn("subject");
        when(mockParameterResolver.resolve(job, "status")).thenReturn("status");

        Field fieldParam = IconRequestHandler.class.getDeclaredField("parameterResolver");
        fieldParam.setAccessible(true);
        fieldParam.set(iconRequestHandler, mockParameterResolver);

        Field fieldIcon = IconRequestHandler.class.getDeclaredField("iconResolver");
        fieldIcon.setAccessible(true);
        fieldIcon.set(iconRequestHandler, mockIconResolver);

        StatusImage statusImage = iconRequestHandler.handleIconRequestForJob(job, "style", "subject", "status");
        assertEquals(mockedStatusImage, statusImage);
    }

    @Test
    void handleIconRequestForJobNull() throws NoSuchFieldException, IllegalAccessException {
        ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
        StatusImage mockedStatusImage = new StatusImage();
        when(mockIconResolver.getImage(BallColor.NOTBUILT, "style", "subject", null, null, null, null))
                .thenReturn(mockedStatusImage);

        Field fieldIcon = IconRequestHandler.class.getDeclaredField("iconResolver");
        fieldIcon.setAccessible(true);
        fieldIcon.set(iconRequestHandler, mockIconResolver);

        StatusImage statusImage = iconRequestHandler.handleIconRequestForJob(null, "style", "subject", "status");
        assertEquals(mockedStatusImage, statusImage);
    }
}
