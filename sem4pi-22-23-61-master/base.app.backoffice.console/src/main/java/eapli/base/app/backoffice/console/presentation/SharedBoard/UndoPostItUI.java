package eapli.base.app.backoffice.console.presentation.SharedBoard;

import eapli.base.SharedBoard.aplication.CreateBoardPostItController;
import eapli.base.SharedBoard.domain.SharedBoard;
import eapli.base.infrastructure.persistence.PersistenceContext;
import eapli.framework.infrastructure.authz.application.AuthorizationService;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.UserSession;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import eapli.framework.io.util.Console;
import eapli.framework.presentation.console.AbstractUI;
import eapli.framework.presentation.console.SelectWidget;

public class UndoPostItUI extends AbstractUI {

    private final AuthorizationService authz = AuthzRegistry.authorizationService();
    final UserSession s = authz.session().orElseThrow(IllegalStateException::new);
    final SystemUser myUser = s.authenticatedUser();

    private final CreateBoardPostItController controller = new CreateBoardPostItController(
            PersistenceContext.repositories().sharedBoardRepository());
    @Override
    protected boolean doShow() {
        Iterable<SharedBoard> sharedBoards = controller.findBoardsBySystemUser(myUser);
        final SelectWidget<SharedBoard> selector = new SelectWidget<>("Shared Boards:", sharedBoards, new SharedBoardPrinter());
        selector.show();
        final SharedBoard sharedBoard = selector.selectedElement();
        if(sharedBoard!=null){
            if(controller.getBoardAccessType(sharedBoard, myUser)) {

                try {
                    controller.undoPostIt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else{
                System.out.println("You don't have permission to undo a post it in this board.");
            }

        }

        return false;
    }

    @Override
    public String headline() {
        return "Undo Post it";
    }
}
