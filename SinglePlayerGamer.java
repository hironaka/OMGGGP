import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.exception.GameAnalysisException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;


public class SinglePlayerGamer extends StateMachineGamer {

	private List<Move> Moves;
	private Set<MachineState> Visited;
	private StateMachine Machine;
	private int Step;
	private int Score;

	private int findBestPlan(Role role, MachineState state, long timeout) 
			throws GoalDefinitionException, 
				   MoveDefinitionException, 
				   TransitionDefinitionException {
		
		long time = System.currentTimeMillis();
		if (timeout < time + 2000) {
			System.out.println(timeout);
			System.out.println(time);
			return 0;
		}
		
		if (Machine.isTerminal(state)) {
			int result = Machine.getGoal(state, role);
			if (Score < result) {
				Moves.clear();
				Score = result;
			}
			
			return result;
		}
		
		Visited.add(state);
		List<Move> actions = Machine.getLegalMoves(state, role);
		Map<Move, List<MachineState>> nextStates = Machine.getNextStates(state, role);
		int score = Integer.MIN_VALUE;
		int actionIndex = 0;
		for (int i = 0; i < actions.size(); i++) {
			Move nextAction = actions.get(i);
			MachineState nextState = nextStates.get(nextAction).get(0);
			if (!Visited.contains(nextState)) {
				int result = findBestPlan(role, nextState, timeout);
				if (result > score) {
					score = result;
					actionIndex = i;
				}
			}
		}
		
		if (Score == score) {
			Moves.add(0, actions.get(actionIndex));
		}
		
		return score;
	}
	
	@Override
	public StateMachine getInitialStateMachine() {
		return new ProverStateMachine();
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		
		Machine = getStateMachine();
		Moves = new ArrayList<Move>();
		Step = 0;
		Score = 0;
		Visited = new HashSet<MachineState>();
		MachineState state = Machine.getInitialState();
		List<Role> roles = Machine.getRoles();
		Role myRole = roles.get(0);
		int score = findBestPlan(myRole, state, timeout);
		System.out.println(score);	
		System.out.println(Moves);
	}
	

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		
		Move move = Moves.get(Step);
		Step ++;
		return move;
	}

	@Override
	public void stateMachineStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateMachineAbort() {
		// TODO Auto-generated method stub

	}

	@Override
	public void analyze(Game g, long timeout) throws GameAnalysisException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Single Player";
	}
	
	@Override
	public DetailPanel getDetailPanel() {
		return new SimpleDetailPanel();
	}

}
