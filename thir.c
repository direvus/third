#include <windows.h>
#include "include/thir.h"

const char class[] = "primary";

LRESULT CALLBACK proc(HWND w, UINT msg, WPARAM wp, LPARAM lp)
{
  switch(msg)
  {
    case WM_CREATE:
      {
	HWND button;
	HBITMAP img;
	UINT x = 10;
	UINT y = 10;
	UINT width = 61;
	UINT height = 52;
	UINT i;
	UINT dice[8] = {2, 4, 6, 8, 10, 12, 20, 100};

	for(i = 0; i < 8; i++)
	{
	  button = CreateWindowEx(WS_EX_LEFT, "BUTTON", "", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_BITMAP | BS_FLAT,
	    x, y, width, height,
	    w, (HMENU) (1000 + dice[i]), GetModuleHandle(NULL), NULL);

	  img = LoadImage(GetModuleHandle(NULL), MAKEINTRESOURCE(2000 + dice[i]), IMAGE_BITMAP,
	    0, 0, 0);

	  if(img != NULL) SendMessage(button, BM_SETIMAGE, (WPARAM) IMAGE_BITMAP, (LPARAM) img);

	  x += width;
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
  wc.hIcon		= NULL;
  wc.hIconSm		= NULL;
  wc.hbrBackground	= (HBRUSH) (COLOR_BTNFACE + 1);
  wc.hCursor		= LoadCursor(NULL, IDC_ARROW);
  wc.lpszMenuName	= NULL;
  wc.style		= 0;

  if(!RegisterClassEx(&wc))
  {
    MessageBox(NULL, "Window class registration failed", "Flagrant System Error", MB_ICONEXCLAMATION | MB_OK);
    return 0;
  }

  w = CreateWindowEx(WS_EX_APPWINDOW, class, "THIR", WS_BORDER | WS_SYSMENU | WS_MINIMIZEBOX, 
    CW_USEDEFAULT, CW_USEDEFAULT, 515, 300,
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
