#define IDI_APP		100
#define IDI_TASKBAR	101

#define IDC_DX		1000
#define IDC_D2		1002
#define IDC_D4		1004
#define IDC_D6		1006
#define IDC_D8		1008
#define IDC_D10		1010
#define IDC_D12		1012
#define IDC_D20		1020
#define IDC_D100	1100

#define IDB_DX		2000
#define IDB_D2		2002
#define IDB_D4		2004
#define IDB_D6		2006
#define IDB_D8		2008
#define IDB_D10		2010
#define IDB_D12		2012
#define IDB_D20		2020
#define IDB_D100	2100

#define IDC_NDX		3000
#define IDC_ND2		3002
#define IDC_ND4		3004
#define IDC_ND6		3006
#define IDC_ND8		3008
#define IDC_ND10	3010
#define IDC_ND12	3012
#define IDC_ND20	3020
#define IDC_ND100	3100

#define IDC_SDX		4000
#define IDC_MULT	4001
#define IDC_MULT_UP	4002
#define IDC_MULT_DOWN	4003
#define IDC_ADD		4004
#define IDC_ADD_UP	4005
#define IDC_ADD_DOWN	4006
#define IDS_MULT	4007
#define IDS_ADD		4008

#define IDC_RESET	4100
#define IDC_ROLL	4101
#define IDC_RESULTS	4102
#define IDS_TOTAL	4103

#define IDC_PRESETS	4200
#define IDC_PS_NEW	4201
#define IDC_PS_UPDATE	4202
#define	IDC_PS_RENAME	4203
#define IDC_PS_DELETE	4204

#define IDD_PS_NEW	4300
#define IDC_PS_NEW_NAME	4301

const unsigned int num_dice = 8;
const unsigned int dice[8] = {2, 4, 6, 8, 10, 12, 20, 100};

LRESULT CALLBACK dice_proc(HWND w, UINT msg, WPARAM wp, LPARAM lp);
BOOL CALLBACK new_preset_proc(HWND w, UINT msg, WPARAM wp, LPARAM lp);

void alter_edit(HWND w, UINT id, int mod);
void alter_edit_u(HWND w, UINT id, int mod);
unsigned long roll(UINT sides);

typedef struct
{
  char name[100];
  unsigned int dice[8];
  unsigned int x_sides;
  unsigned int x_num;
  unsigned int mult;
  int mod;
} conf;

/* Set all the properties of a conf struct
 */
void set_conf(conf * c, char * name, unsigned int d2, unsigned int d4, unsigned int d6, unsigned int d8, unsigned int d10, unsigned int d12, unsigned int d20, unsigned int d100, unsigned int x_sides, unsigned int x_num, unsigned int mult, int mod);

/* Copy all the properties from the src conf to the dest conf
 */
void copy_conf(conf * dest, conf * src);

/* Fill the string with a textual description of the pointed-to conf.
 */
void describe_conf(conf *, char *);

/* Populate the pointed-to conf with current values from the interface.
 */
void current_conf(HWND, conf *);

/* Populate the interface controls with the values from the pointed-to conf.
 */
void load_conf(HWND w, conf * c);

/* Populate the pointed-to conf with values from a specially formatted string.
 */
void import_conf(conf * c, char * str);

/* Dump the pointed-to conf's data to a string that can be re-imported using import_conf()
 */
void export_conf(conf * c, char * str);
