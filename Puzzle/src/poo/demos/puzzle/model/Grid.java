package poo.demos.puzzle.model;

import java.util.List;

/**
 * Class whose instances represent puzzle grids.
 * For the sake of simplification, grids always have a squared shape. 
 */
public class Grid {
	
	/**
	 * Holds the two-dimensional array that holds the puzzle's pieces. 
	 */
	private final Piece[][] grid;
	
	/**
	 * Holds the current position of the grid's empty space. 
	 */
	private Position emptySpacePosition;
	
	/**
	 * The puzzle's size
	 */
	private int size;
	
	/**
	 * Helper method that produces an array of {@link Piece} instances to be used
	 * in the grid.
	 * 
	 * @param size the size of the puzzle's side. The size of the puzzle must be, at least,
	 * of two elements per side. 
	 * @return the instance
	 * @throws IllegalArgumentException if size is less or equal than {@code 1} 
	 * @return The array of {@link MutablePiece} instances to be used
	 * to initialize the grid.
	 */
	private static Piece[] createMutablePieces(int size)
	{
		if(size <= 1)
			throw new IllegalArgumentException();
		
		// Initialize placeholder
		Piece[] pieces = new Piece[size * size - 1];
		
		// Initialize pieces
		for(int idx = 0; idx < pieces.length; ++idx)
		{
			int initialX = idx % size;
			int initialY = idx / size;
			pieces[idx] = new Piece(initialX, initialY);
		}

		return pieces;
	}
	
	/**
	 * Initializes a grid instance with the given size.
	 *  
	 * @param size the size of the square
	 * @throws IllegalArgumentException if size is less or equal than {@code 0} 
	 */
	private Grid(int size)
	{
		this.size = size;
		grid = new Piece[size][size];
		emptySpacePosition = Position.fromCoordinates(size-1, size-1);
	}

	/**
	 * Helper method that checks if a given position is within the grid's bounds.
	 * 
	 * @param position The instance to be checked
	 * @return {@code true} if it is a valid position, {@false} otherwise
	 */
	private boolean isPositionWithinBounds(Position position)
	{
		return !(position.X < 0 || position.X >= size || position.Y < 0 || position.Y >= size);
	}
	
	/**
	 * Helper method that moves the given piece to the specified position, if it is an adjacent one.
	 *  
	 * @param piece The piece to be moved
	 * @param destination The position where the piece is to be moved to
	 * @return {@code true} if the piece has been moved, {@code false} if the piece 
	 * cannot be moved, that is, it is not adjacent to the specified position.
	 */
	private boolean doMoveInternal(Piece piece, Position destination)
	{
		final int TOTAL_DELTA_TO_ADJACENT = 1; 
		int totalDelta = Math.abs((piece.getPosition().X - destination.X)) + Math.abs(piece.getPosition().Y - destination.Y);
		
		// Not moving to the adjacent position
		if(totalDelta != TOTAL_DELTA_TO_ADJACENT)
			return false;
		
		emptySpacePosition = piece.getPosition();
		Piece targetPiece = grid[piece.getPosition().Y][piece.getPosition().X];
		grid[emptySpacePosition.Y][emptySpacePosition.X] = null;
		targetPiece.moveTo(destination);
		grid[piece.getPosition().Y][piece.getPosition().X] = targetPiece;

		return true;
	}
	
	/**
	 * Factory method that produces a shuffled puzzle. 
	 * 
	 * Implementation note: The current algorithm always leaves an empty space
	 * at the bottom rightmost position of the grid.
	 *  
	 * @param size the size of the puzzle's side. The size of the puzzle must be, at least,
	 * of two elements per side. 
	 * @return the shuffled instance
	 * @throws IllegalArgumentException if size is less or equal than {@code 1} 
	 */
	public static Grid createRandomPuzzle(int size)
	{
		// Initialize placeholder
		Piece[] pieces = createMutablePieces(size);
		
		// Initialize grid 
		Grid instance = new Grid(size);
		
		for(int idx = 0; idx < pieces.length; ++idx)
		{
			// Select a piece
			int selectedIdx = (int) (Math.random() * (pieces.length - idx));
			Piece selectedPiece = pieces[selectedIdx];
			pieces[selectedIdx] = pieces[pieces.length - idx - 1];
			
			// Place it
			int currentX = idx % size;
			int currentY = idx / size;
			selectedPiece.moveTo(Position.fromCoordinates(currentX, currentY));
			instance.grid[currentY][currentX] = selectedPiece;
		}
		
		return instance;
	}
	
	/**
	 * Factory method that produces a puzzle with its pieces at their original positions. 
	 * 
	 * @param size the size of the puzzle's side. The size of the puzzle must be, at least,
	 * of two elements per side. 
	 * @return the new instance
	 * @throws IllegalArgumentException if size is less or equal than {@code 1} 
	 */
	public static Grid createPuzzle(int size)
	{
		// Initialize placeholder
		Piece[] pieces = createMutablePieces(size);
		
		// Initialize grid 
		Grid instance = new Grid(size);
		
		for(int idx = 0; idx < pieces.length; ++idx)
			instance.grid[idx / size][idx % size] = pieces[idx];
		
		return instance;
	}
	
	/**
	 * Factory method that produces a puzzle initialized with the given pieces.
	 * 
	 * @param pieces the pieces to add to the puzzle
	 * @param emptyPosition the position of the puzzle that is empty
	 * @return the new instance
	 * @throws IllegalArgumentException if either argument is {@code null} 
	 */
	public static Grid createPuzzle(List<Piece> pieces, Position emptyPosition)
	{
		if(pieces == null || emptyPosition == null)
			throw new IllegalArgumentException();

		int size = (int) Math.sqrt(pieces.size() + 1);
		
		Grid instance = new Grid(size);
		for(Piece piece : pieces)
		{
			Position pos = piece.getPosition();
			instance.grid[pos.Y][pos.X] = new Piece(pos);
		}
		
		instance.emptySpacePosition = emptyPosition;
		
		return instance;
	}
	
	/**
	 * Gets the piece at the given position. If the position is free,
	 * the method returns {@code null}.
	 * 
	 * @param position the {@link Position} instance
	 * @return the piece at the given position, or {@code null} if that position 
	 * is empty
	 * @throws IllegalArgumentException if the given position is not within the 
	 * grid's bounds 
	 */
	public Piece getPieceAtPosition(Position position)
	{
		if(!isPositionWithinBounds(position))
			throw new IllegalArgumentException();
		
		// Note to students: Producing the original piece. Is this a problem?  
		return grid[position.Y][position.X];
	}

	/**
	 * Gets the piece at the given position. If the position is free,
	 * the method returns {@code null}.
	 * 
	 * @param x the horizontal coordinate value (0 < x < puzzleSize)
	 * @param y the vertical coordinate value (0 < y < puzzleSize)
	 * @return the piece at the given position, or {@code null} if that position 
	 * is empty
	 * @throws IllegalArgumentException if the parameters are not within the 
	 * grid's bounds (0 <= value < gridSide) 
	 */
	public Piece getPieceAtPosition(int x, int y)
	{
		return getPieceAtPosition(Position.fromCoordinates(x, y));
	}
	
	/**
	 * Gets the current position of the grid's empty space.
	 * 
	 * @return The current position of the grid's empty space
	 */
	public Position getEmptySpacePosition()
	{
		return emptySpacePosition;
	}

	/**
	 * Gets the grid's size.
	 * 
	 * @return the grid's size
	 */
	public int getSize()
	{
		return grid.length;
	}
	
	/**
	 * Moves the given piece to the given position, assuming that the piece 
	 * is adjacent to it and that it corresponds to an empty space.
	 * 
	 * @param piece The piece to be moved
	 * @param destination The new position
	 * @return {@code true} if the piece has been moved, {@code false} if the piece 
	 * cannot be moved, that is, it is not adjacent to the empty space or if the 
	 * specified position is occupied.
	 */
	public boolean doMove(Piece piece, Position destination)
	{
		if(!isPositionWithinBounds(destination) || !destination.equals(emptySpacePosition))
			return false;
		
		return doMoveInternal(piece, destination);
	}
	
	/**
	 * Moves the given piece to the puzzle's empty space, assuming that the piece 
	 * is adjacent to it.
	 * 
	 * @param piece The piece to be moved
	 * @return {@code true} if the piece has been moved, {@code false} if the piece 
	 * cannot be moved, that is, it is not adjacent to the empty space.
	 */
	public boolean doMove(Piece piece)
	{
		return doMoveInternal(piece, emptySpacePosition);
	}
}
