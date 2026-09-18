package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks that Emma reports whether the last response was a complaint, which is how the
 * window knows to set an error apart from an ordinary reply.
 */
public class EmmaTest {

    @TempDir
    Path tempDir;

    private Emma emma() {
        return new Emma(tempDir.resolve("emma.json").toString());
    }

    @Test
    public void isError_beforeAnyCommand_isFalse() {
        assertFalse(emma().isError());
    }

    @Test
    public void getResponse_unrecognisedCommand_isFlaggedAsAnError() {
        Emma emma = emma();
        assertEquals("Sorry, I don't know what that means!", emma.getResponse("blah"));
        assertTrue(emma.isError());
    }

    @Test
    public void getResponse_commandThatWasCarriedOut_isNotFlagged() {
        Emma emma = emma();
        emma.getResponse("todo read book");
        assertFalse(emma.isError());
    }

    @Test
    public void getResponse_validCommandAfterAnError_clearsTheFlag() {
        Emma emma = emma();
        emma.getResponse("blah");
        assertTrue(emma.isError());
        emma.getResponse("list");
        assertFalse(emma.isError());
    }

    @Test
    public void getResponse_refusedDuplicate_isFlaggedAsAnError() {
        Emma emma = emma();
        emma.getResponse("todo read book");
        emma.getResponse("todo read book");
        assertTrue(emma.isError());
    }

    @Test
    public void getResponse_bye_endsWithoutBeingAnError() {
        Emma emma = emma();
        emma.getResponse("bye");
        assertTrue(emma.isExit());
        assertFalse(emma.isError());
    }
}
