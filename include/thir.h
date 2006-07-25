#define IDC_D2		1002
#define IDC_D4		1004
#define IDC_D6		1006
#define IDC_D8		1008
#define IDC_D10		1010
#define IDC_D12		1012
#define IDC_D20		1020
#define IDC_D100	1100

#define IDB_D2		2002
#define IDB_D4		2004
#define IDB_D6		2006
#define IDB_D8		2008
#define IDB_D10		2010
#define IDB_D12		2012
#define IDB_D20		2020
#define IDB_D100	2100

#define IDC_ND2		3002
#define IDC_ND4		3004
#define IDC_ND6		3006
#define IDC_ND8		3008
#define IDC_ND10	3010
#define IDC_ND12	3012
#define IDC_ND20	3020
#define IDC_ND100	3100

#define IDC_MULT	4000
#define IDC_MULT_UP	4001
#define IDC_MULT_DOWN	4002
#define IDC_ADD		4003
#define IDC_ADD_UP	4004
#define IDC_ADD_DOWN	4005
#define IDS_MULT	4006
#define IDS_ADD		4007

#define IDC_RESET	4100
#define IDC_ROLL	4101
#define IDC_RESULTS	4102
#define IDS_TOTAL	4103

void IncrementEdit(HWND w, UINT id);
void DecrementEdit(HWND w, UINT id);
UINT roll(UINT sides);
