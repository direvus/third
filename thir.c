#include <windows.h>
#include <stdio.h>
#include <time.h>
#include "include/thir.h"
#include "include/mt19937ar.h"

const char class[] = "primary";
WNDPROC oldproc = 0;

LRESULT CALLBACK proc(HWND w, UINT msg, WPARAM wp, LPARAM lp)
{
  switch(msg)
  {
    case WM_CREATE:
      {
	init_genrand(time(NULL));
	HWND button, text, stat, group;
	HBITMAP img;
	HFONT font = GetStockObject(DEFAULT_GUI_FONT);
	UINT x = 12;
	UINT y = 14;
	UINT width = 61;
	UINT height = 52;
	UINT i;

        group = CreateWindowEx(0, "BUTTON", "Dice", WS_CHILD | WS_VISIBLE | BS_GROUPBOX,
	  6, 0, x + (width * num_dice), height + 44,
	  w, NULL, GetModuleHandle(NULL), NULL);
	SendMessage(group, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	for(i = 0; i < num_dice; i++)
	{
	  button = CreateWindowEx(WS_EX_LEFT, "BUTTON", "", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP,
	    x, y, width, height,
	    w, (HMENU) (1000 + dice[i]), GetModuleHandle(NULL), NULL);

	  oldproc = (WNDPROC) SetWindowLongPtr(button, GWLP_WNDPROC, (LONG_PTR) dice_proc);

	  img = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(2000 + dice[i]), IMAGE_BITMAP,
	    0, 0, 0);

	  if(img != NULL) SendMessage(button, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) img);

	  text = CreateWindowEx(WS_EX_RIGHT | WS_EX_CLIENTEDGE, "EDIT", "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | ES_NUMBER,
	    x + 15, y + height + 2, 30, 20,
	    w, (HMENU) (3000 + dice[i]), GetModuleHandle(NULL), NULL);

	  SendMessage(text, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	  SendMessage(text, WM_SETTEXT, 0, (LPARAM) "0");

	  x += width;
	}

	// The dX (custom die) controls 

	x += 10;

	button = CreateWindowEx(WS_EX_LEFT, "BUTTON", "", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP,
	  x, y, width, height,
	  w, (HMENU) IDC_DX, GetModuleHandle(NULL), NULL);

	oldproc = (WNDPROC) SetWindowLongPtr(button, GWLP_WNDPROC, (LONG_PTR) dice_proc);

	img = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(IDB_DX), IMAGE_BITMAP,
	  0, 0, 0);

	if(img != NULL) SendMessage(button, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) img);

	text = CreateWindowEx(WS_EX_RIGHT | WS_EX_CLIENTEDGE, "EDIT", "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | ES_NUMBER,
	  x, y + height + 2, width, 20,
	  w, (HMENU) IDC_SDX, GetModuleHandle(NULL), NULL);

	SendMessage(text, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	SendMessage(text, WM_SETTEXT, 0, (LPARAM) "3");

	text = CreateWindowEx(WS_EX_RIGHT | WS_EX_CLIENTEDGE, "EDIT", "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | ES_NUMBER,
	  x + 15, y + height + 22, 30, 20,
	  w, (HMENU) IDC_NDX, GetModuleHandle(NULL), NULL);

	SendMessage(text, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	SendMessage(text, WM_SETTEXT, 0, (LPARAM) "0");

        group = CreateWindowEx(0, "BUTTON", "", WS_CHILD | WS_VISIBLE | BS_GROUPBOX,
	  x - 4, y - 14, width + 8, height + 60,
	  w, NULL, GetModuleHandle(NULL), NULL);
	SendMessage(group, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	x = 12;
	y += height + 58;

	// Muliplier controls

	text = CreateWindowEx(WS_EX_RIGHT | WS_EX_CLIENTEDGE, "EDIT", "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | ES_NUMBER,
	  x + 20, y, 30, 20,
	  w, (HMENU) IDC_MULT, GetModuleHandle(NULL), NULL);
	SendMessage(text, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	SendMessage(text, WM_SETTEXT, 0, (LPARAM) "1");

	button = CreateWindowEx(0, "BUTTON", "+", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x + 54, y, 20, 20,
	  w, (HMENU) IDC_MULT_UP, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	button = CreateWindowEx(0, "BUTTON", "-", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x + 76, y, 20, 20,
	  w, (HMENU) IDC_MULT_DOWN, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

        group = CreateWindowEx(0, "BUTTON", "Multiplier", WS_CHILD | WS_VISIBLE | BS_GROUPBOX,
	  x, y - 15, 120, 40,
	  w, NULL, GetModuleHandle(NULL), NULL);
	SendMessage(group, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	// Modifier controls
	
	y += 35;

	text = CreateWindowEx(WS_EX_RIGHT | WS_EX_CLIENTEDGE, "EDIT", "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | ES_NUMBER,
	  x + 20, y, 30, 20,
	  w, (HMENU) IDC_ADD, GetModuleHandle(NULL), NULL);
	SendMessage(text, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	SendMessage(text, WM_SETTEXT, 0, (LPARAM) "0");

	button = CreateWindowEx(0, "BUTTON", "+", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x + 54, y, 20, 20,
	  w, (HMENU) IDC_ADD_UP, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	button = CreateWindowEx(0, "BUTTON", "-", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x + 76, y, 20, 20,
	  w, (HMENU) IDC_ADD_DOWN, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

        group = CreateWindowEx(0, "BUTTON", "Modifier", WS_CHILD | WS_VISIBLE | BS_GROUPBOX,
	  x, y - 15, 120, 40,
	  w, NULL, GetModuleHandle(NULL), NULL);
	SendMessage(group, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	// Main buttons: reset and roll
	
	x = 20;
	y += 40;

	button = CreateWindowEx(0, "BUTTON", "Reset", WS_CHILD | WS_VISIBLE | WS_TABSTOP | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x, y, 100, 30,
	  w, (HMENU) IDC_RESET, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	button = CreateWindowEx(0, "BUTTON", "Roll", WS_CHILD | WS_VISIBLE | WS_TABSTOP | BS_DEFPUSHBUTTON | BS_CENTER | BS_FLAT,
	  x, y + 35, 100, 30,
	  w, (HMENU) IDC_ROLL, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	// Results: list box for roll log and static for final display
	
	x = 140;
	y -= 84;

	HWND list;
	list = CreateWindowEx(WS_EX_CLIENTEDGE, "LISTBOX", "", WS_CHILD | WS_VISIBLE | WS_VSCROLL | LBS_NOSEL | LBS_HASSTRINGS,
	  x, y, 170, 140,
	  w, (HMENU) IDC_RESULTS, GetModuleHandle(NULL), NULL);
	SendMessage(list, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	y += 136;

	stat = CreateWindowEx(WS_EX_LEFT, "STATIC", "Result:", WS_CHILD | WS_VISIBLE,
	  x, y, 60, 20,
	  w, NULL, GetModuleHandle(NULL), NULL);
	SendMessage(stat, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	LOGFONT lf;
	GetObject(font, sizeof(lf), &lf);

	lf.lfWeight = FW_BOLD;
	HFONT bold = CreateFontIndirect(&lf);

	stat = CreateWindowEx(WS_EX_RIGHT, "STATIC", "", WS_CHILD | WS_VISIBLE,
	  x + 60, y, 110, 20,
	  w, (HMENU) IDS_TOTAL, GetModuleHandle(NULL), NULL);
	SendMessage(stat, WM_SETFONT, (WPARAM) bold, MAKELPARAM(FALSE, 0));

	// Presets: show list of preconfigured presets and allow them to be quickly loaded and rolled.

	x += 180;
	y -= 136;

	group = CreateWindowEx(0, "BUTTON", "Presets", WS_CHILD | WS_VISIBLE | BS_GROUPBOX,
	  x, y, 574 - x, 280 - y,
	  w, NULL, GetModuleHandle(NULL), NULL);
	SendMessage(group, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	conf c;
	c.dice[2] = 3;
	c.dice[3] = 1;

	MessageBox(NULL, describe_conf(&c), "Config", MB_OK);
      }
      break;

    case WM_COMMAND:
      {
	UINT ctl = LOWORD(wp);
	switch(ctl)
	{
	  case IDC_ROLL:
	    {
	      UINT i, item, mult;
	      unsigned long r, total;
	      INT mod;
	      char buf[100];

	      while(SendDlgItemMessage(w, IDC_RESULTS, LB_GETCOUNT, 0, 0) > 0)
	      {
		SendDlgItemMessage(w, IDC_RESULTS, LB_DELETESTRING, 0, 0);
	      }

	      total = 0;

	      for(i = 0; i < num_dice; i++)
	      {
		UINT n = GetDlgItemInt(w, dice[i] + 3000, NULL, FALSE);
		if(n <= 0) continue;

		UINT j = 1;
		while(j <= n)
		{
		  r = roll(dice[i]);

		  sprintf(buf, "d%d roll #%d = %ld", dice[i], j, r);
		  item = SendDlgItemMessage(w, IDC_RESULTS, LB_ADDSTRING, 0, (LPARAM) buf);

		  total += r;
		  j++;
		}
	      }

	      UINT s = GetDlgItemInt(w, IDC_SDX, NULL, FALSE);
	      i = GetDlgItemInt(w, IDC_NDX, NULL, FALSE);

	      if(s > 0 && i > 0)
	      {
		UINT j = 1;
		while(j <= i)
		{
		  r = roll(s);

		  sprintf(buf, "d%d roll #%d = %ld", s, j, r);
		  item = SendDlgItemMessage(w, IDC_RESULTS, LB_ADDSTRING, 0, (LPARAM) buf);

		  total += r;
		  j++;
		}
	      }

	      mult = GetDlgItemInt(w, IDC_MULT, NULL, FALSE);
	      if(mult > 1)
	      {
		i = total;
		total *= mult;

		sprintf(buf, "%d multiplied by %d = %ld", i, mult, total);
		item = SendDlgItemMessage(w, IDC_RESULTS, LB_ADDSTRING, 0, (LPARAM) buf);
	      }

	      mod = GetDlgItemInt(w, IDC_ADD, NULL, TRUE);
	      if(mod != 0)
	      {
		i = total;
		total += mod;

		sprintf(buf, "%d modified by %+d = %ld", i, mod, total);
		item = SendDlgItemMessage(w, IDC_RESULTS, LB_ADDSTRING, 0, (LPARAM) buf);
	      }

	      SetDlgItemInt(w, IDS_TOTAL, total, FALSE);
	    }
	    break;

	  case IDC_MULT_UP:

	    alter_edit_u(w, IDC_MULT, 1);
	    break;

	  case IDC_MULT_DOWN:

	    alter_edit_u(w, IDC_MULT, -1);
	    break;

	  case IDC_ADD_UP:

	    alter_edit(w, IDC_ADD, 1);
	    break;

	  case IDC_ADD_DOWN:

	    alter_edit(w, IDC_ADD, -1);
	    break;

	  case IDC_RESET:
	    {
	      UINT i;

	      for(i = 0; i < num_dice; i++)
	      {
		SetDlgItemInt(w, dice[i] + 3000, 0, FALSE);
	      }

	      SetDlgItemInt(w, IDC_MULT, 1, FALSE);
	      SetDlgItemInt(w, IDC_ADD, 0, FALSE);
	    }
	    break;
	}
      }
      break;

    case WM_CLOSE:

      DestroyWindow(w);
      break;

    case WM_DESTROY:

      PostQuitMessage(0);
      break;

    default:

      return DefWindowProc(w, msg, wp, lp);
  }

  return 0;
}

LRESULT CALLBACK dice_proc(HWND w, UINT msg, WPARAM wp, LPARAM lp)
{
  /* Intercepts messages sent to the dice buttons.
   * Left-clicking increases the corresponding dice counter by one, right-clicking decreases it by one.
   * Holding shift while clicking changes the counter by five.
   *
   * The window IDs are structured such that the counter is always the ID of the button plus 2000.
   * I use GetMenu to find the ID (which is stored in the HMENU slot) of this button.
   */ 
  switch(msg)
  {
    case WM_LBUTTONDOWN:
    case WM_RBUTTONDOWN:
    case WM_LBUTTONDBLCLK:
    case WM_RBUTTONDBLCLK:
      {
	UINT i = 1;

	if(wp & MK_SHIFT) i = 5;
	if(msg == WM_RBUTTONDOWN || msg == WM_RBUTTONDBLCLK) i = -i;

	alter_edit_u(GetParent(w), (UINT) GetMenu(w) + 2000, i);
      }
      break;

    default:

      return CallWindowProc(oldproc, w, msg, wp, lp);
  }

  return 0;
}

int WINAPI WinMain (HINSTANCE inst, HINSTANCE prev_inst, PSTR opts, int show) 
{
  WNDCLASSEX wc;
  HWND w;
  MSG msg;

  wc.lpszClassName	= class;
  wc.lpfnWndProc	= proc;
  wc.hInstance		= inst;
  wc.cbSize		= sizeof(WNDCLASSEX);
  wc.cbClsExtra		= 0;
  wc.cbWndExtra		= 0;
  wc.hIcon		= LoadIcon(inst, MAKEINTRESOURCE(IDI_APP));
  wc.hIconSm		= LoadIcon(inst, MAKEINTRESOURCE(IDI_TASKBAR));
  wc.hbrBackground	= (HBRUSH) (COLOR_BTNFACE + 1);
  wc.hCursor		= LoadCursor(NULL, IDC_ARROW);
  wc.lpszMenuName	= NULL;
  wc.style		= 0;

  if(!RegisterClassEx(&wc))
  {
    MessageBox(NULL, "Window class registration failed", "Flagrant System Error", MB_ICONEXCLAMATION | MB_OK);
    return 0;
  }

  w = CreateWindowEx(WS_EX_APPWINDOW | WS_EX_CONTROLPARENT, class, "THIR", WS_BORDER | WS_SYSMENU | WS_MINIMIZEBOX, 
    CW_USEDEFAULT, CW_USEDEFAULT, 590, 320,
    NULL, NULL, inst, NULL);

  if(w == NULL)
  {
    MessageBox(NULL, "Window creation with CreateWindowEx() failed.", "Flagrant System Error", MB_ICONEXCLAMATION | MB_OK);
    return 0;
  }

  ShowWindow(w, show);
  UpdateWindow(w);

  while(GetMessage(&msg, NULL, 0, 0) > 0)
  {
    TranslateMessage(&msg);
    DispatchMessage(&msg);
  }

  return msg.wParam;
}

void alter_edit(HWND w, UINT id, int mod)
{
  SetDlgItemInt(w, id, GetDlgItemInt(w, id, NULL, TRUE) + mod, TRUE);
}

void alter_edit_u(HWND w, UINT id, int mod)
{
  int n = GetDlgItemInt(w, id, NULL, FALSE) + mod;
  if(n < 0) n = 0;

  SetDlgItemInt(w, id, n, FALSE);
}

unsigned long roll(UINT sides)
{
  return (genrand_int32() % sides) + 1;
}

char * describe_conf(conf * c)
{
  char str[1000] = "";
  unsigned int i;
  unsigned int active = 0;

  for(i = 0; i < num_dice; i++)
  {
    if(c->dice[i] > 0)
    {
      if(active > 0) strcat(str, " + ");

      char d[25];
      sprintf(d, "%dd%d", c->dice[i], dice[i]);

      strcat(str, d);

      active++;
    }
  }

  return str;
}
