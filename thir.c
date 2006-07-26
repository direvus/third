#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include "include/thir.h"

const char class[] = "primary";
UINT num_dice = 8;
UINT dice[8] = {2, 4, 6, 8, 10, 12, 20, 100};

LRESULT CALLBACK proc(HWND w, UINT msg, WPARAM wp, LPARAM lp)
{
  switch(msg)
  {
    case WM_CREATE:
      {
	HWND button, text;
	HBITMAP img;
	HFONT font = GetStockObject(DEFAULT_GUI_FONT);
	UINT x = 10;
	UINT y = 10;
	UINT width = 61;
	UINT height = 52;
	UINT i;

	for(i = 0; i < num_dice; i++)
	{
	  button = CreateWindowEx(WS_EX_LEFT, "BUTTON", "", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP | BS_FLAT,
	    x, y, width, height,
	    w, (HMENU) (1000 + dice[i]), GetModuleHandle(NULL), NULL);

	  img = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(2000 + dice[i]), IMAGE_BITMAP,
	    0, 0, 0);

	  if(img != NULL) SendMessage(button, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) img);

	  text = CreateWindowEx(WS_EX_RIGHT | WS_EX_CLIENTEDGE, "EDIT", "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | ES_NUMBER,
	    x + 10, y + height + 2, 30, 20,
	    w, (HMENU) (3000 + dice[i]), GetModuleHandle(NULL), NULL);

	  SendMessage(text, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	  SendMessage(text, WM_SETTEXT, 0, (LPARAM) "0");

	  x += width;
	}

	x = 40;
	y += height + 40;
	HWND stat;

	// Muliplier controls

	stat = CreateWindowEx(WS_EX_LEFT, "STATIC", "Multiplier: ", WS_CHILD | WS_VISIBLE,
	  x, y + 3, 60, 20,
	  w, (HMENU) IDS_MULT, GetModuleHandle(NULL), NULL);
	SendMessage(stat, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	text = CreateWindowEx(WS_EX_RIGHT | WS_EX_CLIENTEDGE, "EDIT", "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | ES_NUMBER,
	  x + 70, y, 30, 20,
	  w, (HMENU) IDC_MULT, GetModuleHandle(NULL), NULL);
	SendMessage(text, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	SendMessage(text, WM_SETTEXT, 0, (LPARAM) "1");

	button = CreateWindowEx(0, "BUTTON", "+", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x + 104, y, 20, 20,
	  w, (HMENU) IDC_MULT_UP, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	button = CreateWindowEx(0, "BUTTON", "-", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x + 126, y, 20, 20,
	  w, (HMENU) IDC_MULT_DOWN, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

        CreateWindowEx(0, "BUTTON", "", WS_CHILD | WS_VISIBLE | BS_GROUPBOX,
	  x - 30, y - 15, 206, 40,
	  w, NULL, GetModuleHandle(NULL), NULL);

	// Modifier controls
	
	y += 35;

	stat = CreateWindowEx(WS_EX_LEFT, "STATIC", "Modifier: ", WS_CHILD | WS_VISIBLE,
	  x, y + 3, 60, 20,
	  w, (HMENU) IDS_MULT, GetModuleHandle(NULL), NULL);
	SendMessage(stat, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	text = CreateWindowEx(WS_EX_RIGHT | WS_EX_CLIENTEDGE, "EDIT", "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | ES_NUMBER,
	  x + 70, y, 30, 20,
	  w, (HMENU) IDC_ADD, GetModuleHandle(NULL), NULL);
	SendMessage(text, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	SendMessage(text, WM_SETTEXT, 0, (LPARAM) "0");

	button = CreateWindowEx(0, "BUTTON", "+", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x + 104, y, 20, 20,
	  w, (HMENU) IDC_ADD_UP, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));
	button = CreateWindowEx(0, "BUTTON", "-", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_CENTER | BS_FLAT,
	  x + 126, y, 20, 20,
	  w, (HMENU) IDC_ADD_DOWN, GetModuleHandle(NULL), NULL);
	SendMessage(button, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

        CreateWindowEx(0, "BUTTON", "", WS_CHILD | WS_VISIBLE | BS_GROUPBOX,
	  x - 30, y - 15, 206, 40,
	  w, NULL, GetModuleHandle(NULL), NULL);

	// Main buttons: reset and roll
	
	x = 50;
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
	
	x = 250;
	y -= 80;

	HWND list;
	list = CreateWindowEx(WS_EX_CLIENTEDGE, "LISTBOX", "", WS_CHILD | WS_VISIBLE | WS_VSCROLL | LBS_NOSEL | LBS_HASSTRINGS,
	  x, y, 200, 160,
	  w, (HMENU) IDC_RESULTS, GetModuleHandle(NULL), NULL);
	SendMessage(list, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	y += 156;

	stat = CreateWindowEx(WS_EX_LEFT, "STATIC", "Result:", WS_CHILD | WS_VISIBLE,
	  x, y, 60, 20,
	  w, NULL, GetModuleHandle(NULL), NULL);
	SendMessage(stat, WM_SETFONT, (WPARAM) font, MAKELPARAM(FALSE, 0));

	LOGFONT lf;
	GetObject(font, sizeof(lf), &lf);

	lf.lfWeight = FW_BOLD;
	HFONT bold = CreateFontIndirect(&lf);

	stat = CreateWindowEx(WS_EX_RIGHT, "STATIC", "", WS_CHILD | WS_VISIBLE,
	  x + 60, y, 140, 20,
	  w, (HMENU) IDS_TOTAL, GetModuleHandle(NULL), NULL);
	SendMessage(stat, WM_SETFONT, (WPARAM) bold, MAKELPARAM(FALSE, 0));
      }
      break;

    case WM_COMMAND:
      {
	UINT ctl = LOWORD(wp);
	if(ctl > 1000 && ctl < 2000)
	{
	  IncrementEdit(w, ctl + 2000);
	}
	else
	{
	  switch(ctl)
	  {
	    case IDC_ROLL:
	      {
		srand(time(NULL));
		UINT i, r, total, item, mult;
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

		    sprintf(buf, "d%d roll #%d = %d", dice[i], j, r);
		    item = SendDlgItemMessage(w, IDC_RESULTS, LB_ADDSTRING, 0, (LPARAM) buf);

		    total += r;
		    j++;
		  }
		}

		mult = GetDlgItemInt(w, IDC_MULT, NULL, FALSE);
		if(mult > 1)
		{
		  total *= mult;

		  sprintf(buf, "Multiplied by %d = %d", mult, total);
		  item = SendDlgItemMessage(w, IDC_RESULTS, LB_ADDSTRING, 0, (LPARAM) buf);
		}

		mod = GetDlgItemInt(w, IDC_ADD, NULL, TRUE);
		if(mod != 0)
		{
		  total += mod;

		  sprintf(buf, "Modified by %+d = %d", mod, total);
		  item = SendDlgItemMessage(w, IDC_RESULTS, LB_ADDSTRING, 0, (LPARAM) buf);
		}

		SetDlgItemInt(w, IDS_TOTAL, total, FALSE);
	      }
	      break;

	    case IDC_MULT_UP:

	      IncrementEdit(w, IDC_MULT);
	      break;

	    case IDC_MULT_DOWN:

	      DecrementEdit(w, IDC_MULT);
	      break;

	    case IDC_ADD_UP:
	      {
		INT n = GetDlgItemInt(w, IDC_ADD, NULL, TRUE);
		SetDlgItemInt(w, IDC_ADD, n + 1, TRUE);
	      }
	      break;

	    case IDC_ADD_DOWN:
	      {
		INT n = GetDlgItemInt(w, IDC_ADD, NULL, TRUE);
		SetDlgItemInt(w, IDC_ADD, n - 1, TRUE);
	      }
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
    CW_USEDEFAULT, CW_USEDEFAULT, 515, 320,
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

void IncrementEdit(HWND w, UINT id)
{
  UINT n = GetDlgItemInt(w, id, NULL, FALSE);
  SetDlgItemInt(w, id, n + 1, FALSE);
}

void DecrementEdit(HWND w, UINT id)
{
  UINT n = GetDlgItemInt(w, id, NULL, FALSE);
  if(n > 0)
  {
    SetDlgItemInt(w, id, n - 1, FALSE);
  }
  else
  {
    SetDlgItemInt(w, id, 0, FALSE);
  }
}

UINT roll(UINT sides)
{
  return (rand() % sides) + 1;
}
