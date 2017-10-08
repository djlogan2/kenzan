package david.logan.kenzan.db;

//
//	The employee status enumeration
//	The business requirement was that new employees were ACTIVE by default,
//		so we put ACTIVE first in the enumeration under the assumption that if
//		the field is never explicitly set, it should be zero, and as such, ACTIVE
//		by default.
//
public enum Status {
	ACTIVE,   // Active is the default state by way of default values generally being zero
	INACTIVE
}
