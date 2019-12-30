namespace Algorithms
{ 
	/// <summary>
	/// Interface for locating the positions of items within a data structure, such as a <see cref="PrioQueueMod{T}"/>.
	/// </summary>
	/// <typeparam name="TItem">The type of the item to locate.</typeparam>
	/// <typeparam name="TLocation">The type of a location.</typeparam>
	public interface ILocator<TItem, TLocation>
	{
		TLocation DefaultLocation { get; } 
		TLocation Get(TItem id);
		void Set(TItem id, TLocation location);
		void Remove(TItem id);
	}

	/// <summary>
	/// An implementation of the <see cref="ILocator{TItem,TLocation}"/> pattern which uses an array to find locations of ints.
	/// </summary>
	public class ArrayLocator<TLocation> : ILocator<int, TLocation>
	{
		private readonly TLocation _defaultLocation;
		private readonly TLocation[] _locations;

		public TLocation DefaultLocation { get { return _defaultLocation; } }

		public ArrayLocator(int maxCapacity)
			: this (maxCapacity, default(TLocation))
		{
		}

		public ArrayLocator(int maxCapacity, TLocation defaultLocation)
		{
			_defaultLocation = defaultLocation;
			_locations = new TLocation[maxCapacity];
			for (int i = 0; i < maxCapacity; i++)
			{
				_locations[i] = defaultLocation;
			}
		}

		public TLocation Get(int id)
		{
			return _locations[id];
		}

		public void Set(int id, TLocation location)
		{
			_locations[id] = location;
		}

		public void Remove(int id)
		{
			_locations[id] = _defaultLocation;
		}
	}
}